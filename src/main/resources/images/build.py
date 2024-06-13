import argparse
import json
from concurrent.futures import ThreadPoolExecutor, as_completed
import subprocess
from subprocess import STDOUT, PIPE
import datetime
import sys
import os
import re
import threading

class DockerImage:
    """
    Helper class which holds details about a docker Image. Can be built and pushed
    """
    def __init__(self, image_name, dockerfile, version, context, second_version, instances, image_version, build_args, docker_repo, target, tag, library_name, latest: bool, counter: int):
        self.image_name = image_name
        self.dockerfile = dockerfile
        self.version = version
        self.context = context
        self.second_version = second_version
        self.instance = instances
        self.image_version = image_version
        self.build_args = build_args
        self.docker_repo = docker_repo
        self.target = target
        self.tag = tag
        self.library_name = library_name
        self.latest = latest
        self.build_command = ''
        self.counter = counter

        # set '/' if its missing from the docke repo
        if self.docker_repo != '' and self.docker_repo[-1] != '/':
            self.docker_repo = self.docker_repo + '/'

        # embed version number in image version
        self.image_version = self.image_version.format(v=self.version, w=self.second_version, nss_hack=self.version[:-4].replace("_","."))

        # construct tag
        self.version_tag = self.tag.format(r=self.docker_repo, n=self.image_name, i=self.instance,v=self.image_version, nss_hack=self.version[:-4].replace("_","."))
    
    def exists(self):
        return not self.exec_docker_command("docker images -q " +  self.version_tag).stdout.strip() == ""

    def build(self):
        # construct string from build args
        build_args_str = ''
        for build_arg, build_arg_value in self.build_args.items():
            # replace any {v} in the build args with the current version
            build_arg_value = build_arg_value.format(v=self.version, w=self.second_version, nss_hack=self.version[:-4].replace("_","."))
            build_args_str += ' --build-arg {}={}'.format(build_arg, build_arg_value)
        
        # construct tag string
        if self.latest:
            # optionally tag as latest
            latest_tag = self.tag.format(r=self.docker_repo, n=self.image_name, i=self.instance, v='latest')
            tags =  ' -t ' + self.version_tag + ' -t ' + latest_tag
        else:
            tags = ' -t ' + self.version_tag

        #construct target string
        if self.target != '':
            target = ' --target ' + self.target.format(n=self.image_name, i=self.instance, v=self.image_name, nss_hack=self.version[:-4].replace("_","."))
        else:
            target = ''

        # construct dockerfile string
        dockerfile = ' -f ' + self.dockerfile

        # construct build context
        context = self.library_name
        if self.context != "":
            context += "/" + self.context

        self.build_command = 'docker build{build_args}{tags}{dockerfile}{target} --no-cache {folder_name}'.format(build_args=build_args_str, tags=tags, dockerfile=dockerfile, target=target, folder_name=context)
        return self.exec_docker_command(self.build_command).returncode
    
    def push(self):
        complete = self.exec_docker_command('docker push {}'.format(self.version_tag)).returncode
        if self.latest:
            complete |= self.exec_docker_command('docker push {}'.format('{docker_repo}{name}-{instance}:latest'.format(self.docker_repo, self.image_name, self.instance))).returncode
        return complete

    def exec_docker_command(self, command: str):
        return subprocess.run(command.split(' '), stdout=PIPE, stderr=STDOUT, encoding="utf-8")

class LibraryBuilder:

    def __init__(self, build_file, parallel_builds, libraries, force_rebuilds, versions, docker_repo):   
        self.folder = os.path.abspath(os.path.dirname(__file__))
        self.log_succeed = open(os.path.join(self.folder, "build_succeeded.log"), "w")
        self.log_failed = open(os.path.join(self.folder, "build_failed.log"), "w")
        self.log_counter_lock = threading.Lock()
        self.build_file = build_file
        self.parallel_builds = parallel_builds
        self.libraries = libraries
        self.force_rebuilds = force_rebuilds
        self.versions_regex = versions
        self.docker_repo = docker_repo
        self.counter = 0

    def warn(self, log):
        print("{}\033[1;33m [!️] {}\033[0m".format(datetime.datetime.now().isoformat(timespec='seconds'), log))

    def error(self, log):
        print("{}\033[1;31m [-] {}\033[0m".format(datetime.datetime.now().isoformat(timespec='seconds'), log))

    def success(self, log):
        print("{}\033[1;32m [+] {}\033[0m".format(datetime.datetime.now().isoformat(timespec='seconds'), log))

    def info(self, log):
        print("{}\033[1;34m [i] {}\033[0m".format(datetime.datetime.now().isoformat(timespec='seconds'), log))

    def docker_images_from_build_group(self, build_group: dict, docker_repo: str, library_name: str, latest: str):
        """
        Yields a group of docker images to be built
        """
        image_name = build_group['name']
        dockerfile = build_group['dockerfile']
        versions = build_group['versions']
        # hack for nss
        try:
            second_versions = build_group['second_versions']
        except:
            second_versions = len(versions) * [None]
        try:
            context = build_group['context']
        except:
            context = ""
        instances = build_group['instances']
        image_version = build_group['image_version']
        build_args = build_group['build_args']
        target = build_group['target']
        tag = build_group['tag']

        dockerfile = '{}/{}'.format(library_name, dockerfile)

        for i in range(len(versions)):
            version = versions[i]
            second_version = second_versions[i]
            _image_version = image_version.format(v=version, nss_hack=version[:-4].replace("_","."))
            try:
                regex = self.versions_regex[library_name]
            except KeyError:
                regex = ''
            # match against version regex
            if bool(re.match(regex, _image_version)):
                is_latest = latest == _image_version
                for instance in instances:
                    self.counter += 1
                    yield DockerImage(image_name, dockerfile, version, context, second_version, instance, image_version, build_args, docker_repo, target, tag, library_name, is_latest, self.counter)



    def parse_library(self, library: str):
        images = []

        library_json = '{library}/build.json'.format(library=library)
        # load dict from json
        try:
            with open(library_json, 'r') as f:
                json_obj = dict(json.load(f))
        except Exception as err:
            self.error("Could not open {library}/build.json".format(library=library))
            self.error(err)
            return images
        build_groups = json_obj['build_groups']
        # helpful for tagging the last image with :latest as well
        latest_version = json_obj['latest']
        for _, build_group_dict in build_groups.items():
            images += [x for x in self.docker_images_from_build_group(build_group_dict, self.docker_repo, library, latest_version)]
        return images


    def execute_bulk(self, image: DockerImage):
        # skip if existent and we dont want to rebuild
        if image.exists() and not self.force_rebuilds:
            self.warn(("{}/{}, build skipped: {} ").format(image.counter, len(self.futures), image.version_tag))
            return
        # build always
        if image.build() == 0:
            self.success(("{}/{}, build succeeded: {} ").format(image.counter, len(self.futures), image.version_tag))
            # and push if necessary
            if self.docker_repo != '':
                if image.push() == 0:
                    self.success(("{}/{}, push succeeded: {} ").format(image.counter, len(self.futures), image.version_tag))
                else:
                    self.error(("{}/{}, push failed: {} Did you use docker login?").format(image.counter, len(self.futures), image.version_tag))
        else:
            self.error(("{}/{}, build failed: {} ").format(image.counter, len(self.futures), image.build_command))


    def build(self):
        with open(self.build_file, 'r') as f:
            libraries = dict(json.load(f))['Libraries']

        # filter libraries to libraries given in ARGS
        if self.libraries != []:
            libraries = list(filter(lambda x: x in self.libraries, libraries))

        self.info("Gathering docker commands from JSON files")

        # parse all library jsons into docker commands
        images_to_process = []
        for library in libraries:
            images_to_process += self.parse_library(library)
        
        # execute ./baseimage/build-base.sh script
        # the resulting docker image is needed as base image for other docker files
        self.info("Building base image")
        completed = subprocess.run(os.path.join(self.folder, "baseimage", "build-base.sh"), stdout=PIPE, stderr=STDOUT, encoding="utf-8")
        if completed.returncode != 0:
            self.error("Building base image failed!")
            print(completed.stdout)
            sys.exit(1)
        else:
            self.success("Successfully built base image")


        self.info("Starting to build {} docker images on {} thread pools.".format(len(images_to_process), self.parallel_builds))

        # put everything into a ThreadPoolExecutor
        self.futures = []
        with ThreadPoolExecutor(self.parallel_builds) as executor:
            for image in images_to_process:
                self.futures.append(executor.submit(self.execute_bulk, image))

        if len(self.futures) == 0:
            self.error("No images found that match your request...")
            sys.exit(1)

        # collect all futures
        for _ in as_completed(self.futures):
            pass
        self.success("+++++++++++++++ Finished building the library +++++++++++++++")            


def main():
    parser = argparse.ArgumentParser(description="Build docker images for all TLS libraries or for specific ones.")
    parser.add_argument("-p", "--parallel_builds", help="Number of parallel docker build operations", default=os.cpu_count()//2, type=int)
    parser.add_argument("-l", "--library", help="Build only docker images of a certain library. " +
                                                "The value is matched against the subfolder names inside the images folder. " +
                                                "Can be specified multiple times. Use Regex for version filtering. " +
                                                "E.g.: -l bearssl:0.* or -l bearssl", default=[], action="append")
    parser.add_argument("-f", "--force_rebuild", help="Build docker containers, even if they already exist.", default=False, action="store_true")
    parser.add_argument("-d", "--deploy", help="Deploy the project to a given repository. Be sure to use docker login and logout yourself", default='', type=str)

    ARGS = parser.parse_args()

    libraries = []
    versions = {}
    for library in ARGS.library:
        try:
            library_name, version = library.split(':')
        except ValueError:
            # no : specified
            version = ''
            library_name = library
        libraries.append(library_name)
        versions[library_name] = version

    builder = LibraryBuilder('libraries.json', ARGS.parallel_builds, libraries, ARGS.force_rebuild, versions, ARGS.deploy)
    builder.build()
        
if __name__ == '__main__':
    main()