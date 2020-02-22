#!/usr/bin/env python3

import argparse
import datetime
import multiprocessing
import os
import subprocess
import sys
from concurrent.futures import ProcessPoolExecutor, as_completed
from subprocess import STDOUT, PIPE
import math

if sys.version_info.major < 3 and sys.version_info.minor < 7:
    print("Requires at least Python 3.7")
    sys.exit(1)

FOLDER = os.path.abspath(os.path.dirname(__file__))
LOG_SUCCEED = open(os.path.join(FOLDER, "build_succeeded.log"), "w")
LOG_FAILED = open(os.path.join(FOLDER, "build_failed.log"), "w")

# Print iterations progress
def printProgressBar(iteration, total, prefix='', suffix='', decimals=1, length=100, fill='█', printEnd="\r"):
    """
    Call in a loop to create terminal progress bar
    @params:
        iteration   - Required  : current iteration (Int)
        total       - Required  : total iterations (Int)
        prefix      - Optional  : prefix string (Str)
        suffix      - Optional  : suffix string (Str)
        decimals    - Optional  : positive number of decimals in percent complete (Int)
        length      - Optional  : character length of bar (Int)
        fill        - Optional  : bar fill character (Str)
        printEnd    - Optional  : end character (e.g. "\r", "\r\n") (Str)
    """
    percent = ("{0:." + str(decimals) + "f}").format(100 * (iteration / float(total)))
    filledLength = int(length * iteration // total)
    bar = fill * filledLength + '-' * (length - filledLength)
    print('\r%s |%s| %s%% %s' % (prefix, bar, percent, suffix), end=printEnd)
    # Print New Line on Complete
    if iteration == total:
        print()

def warn(log):
    print("{}\033[1;33m [!️] {}\033[0m".format(datetime.datetime.now().isoformat(timespec='seconds'), log))

def error(log):
    print("{}\033[1;31m [-] {}\033[0m".format(datetime.datetime.now().isoformat(timespec='seconds'), log))

def success(log):
    print("{}\033[1;32m [+] {}\033[0m".format(datetime.datetime.now().isoformat(timespec='seconds'), log))

def info(log):
    print("{}\033[1;34m [i] {}\033[0m".format(datetime.datetime.now().isoformat(timespec='seconds'), log))



LOG_WRITE_LOCK = multiprocessing.Lock()
def execute_docker(cmd, cwd):
    # parse the docker build command to get
    # the tag of the docker image, set with docker build -t [TAG]
    tag = ""
    flags = ["-t", "--tag"]
    for i in flags:
        if i in cmd:
            tag = cmd[cmd.index(i) + 1]
            break

    complete = subprocess.run(cmd, cwd=cwd, stdout=PIPE, stderr=STDOUT, encoding="utf-8")

    with LOG_WRITE_LOCK:
        if complete.returncode == 0:
            LOG_SUCCEED.writelines([tag])
        else:
            LOG_FAILED.writelines(["[!-!] Failed to build {}".format(tag), complete.stdout])

    return complete.returncode, tag


def main():
    parser = argparse.ArgumentParser(description="Build docker images for all TLS libraries or for specific ones.")
    parser.add_argument("--skip_cmd_generation", help="Skips the regeneration of the docker build commands", action="store_true", default=False)
    parser.add_argument("-p", "--parallel_builds", help="Number of paralllel docker build operations", default=None, type=int)
    parser.add_argument("-l", "--library", help="Build only docker images of a certain library. " +
                                                "The value is matched against the subfolder names inside the images folder. " +
                                                "Can be specified multiple times.", default=[], action="append")

    ARGS = parser.parse_args()

    build_scripts = []
    # receive all build.sh scripts in all subfolders
    for (dirpath, dirnames, filenames) in os.walk(FOLDER):
        scripts = list(filter(lambda x: x == "build.sh", filenames))
        scripts = list(map(lambda x: os.path.join(dirpath, x), scripts))

        build_scripts += scripts


    # filter for specific libraries
    if len(ARGS.library) > 0:
        def script_belongs_to_library(script):
            ret = False
            library_name = os.path.relpath(script, FOLDER).split('/')[0]
            for i in ARGS.library:
                ret = ret or (i.lower() == library_name.lower())
            return ret

        build_scripts = list(filter(script_belongs_to_library, build_scripts))
        if len(build_scripts) == 0:
            error("No TLS Libraries found matching the pattern!")
            sys.exit(1)


    # cmds.sh is a shell script, that contains every docker build command that is needed
    # to build all images
    cmds_path = os.path.join(FOLDER, "cmds.sh")
    if not ARGS.skip_cmd_generation or not os.path.exists(cmds_path):
        info("Generating docker build commands...")
        try:
            os.unlink(cmds_path)
        except:
            pass

        completed = 0
        env = os.environ.copy()
        env["CMD_GENERATION_MODE"] = "1"

        for i in build_scripts:
            # execute every build.sh script
            # with CMD_GENERATION_MODE=1, every docker command gets recorded and written to cmds.sh
            # see helper-functions.sh for implementation details
            subprocess.run(i, capture_output=True, env=env)
            completed += 1
            printProgressBar(completed, len(build_scripts), length=20)


    # execute ./baseimage/build-base.sh script
    # the resulting docker image is needed as base image for other docker files
    info("Building base image...")
    completed = subprocess.run(os.path.join(FOLDER, "baseimage", "build-base.sh"), stdout=PIPE, stderr=STDOUT, encoding="utf-8")
    if completed.returncode != 0:
        error("Building base image failed!")
        print(completed.stdout)
        sys.exit(1)


    # read the generated cmds.sh file
    # every line contains a bash command
    info("Executing Docker build commands...")
    with open(cmds_path, "r") as f:
        cmds = f.read()
        cmds = cmds.strip()
        cmds = cmds.splitlines()

    if len(cmds) % 2 != 0:
        error("Something went wrong...")
        sys.exit(1)


    futures = []
    completed = 0
    # execute multiple docker build commands in parallel
    with ProcessPoolExecutor(ARGS.parallel_builds) as executor:

        for i in range(0, len(cmds), 2):
            # first line contains a 'cd ' bash command to the directory containing Dockerfile(s)
            cwd = cmds[i][3:].strip()
            # second line contains the docker build command
            build_cmd = list(map(lambda x: x.strip(), cmds[i+1].split(" ")))

            # execute the docker build command with the executor
            futures.append(executor.submit(execute_docker, build_cmd, cwd))

        for future in as_completed(futures):
            # is executed, when an executor is finished
            completed += 1
            returnCode, tag = future.result()

            digits = str(math.ceil(math.log10(len(futures))))
            if returnCode != 0:
                error(("{:" + digits + "d}/{}, build failed: {} ").format(completed, len(futures), tag))
            else:
                success(("{:" + digits + "d}/{}, build succeeded: {} ").format(completed, len(futures), tag))



if __name__ == '__main__':
    main()
    print("\nFinished!")
    LOG_FAILED.close()
    LOG_SUCCEED.close()
