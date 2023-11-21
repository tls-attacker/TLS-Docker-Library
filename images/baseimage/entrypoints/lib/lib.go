package lib

import (
	"fmt"
	"net/http"
	"strconv"
	"strings"
	"os"
	"os/exec"
	"github.com/phayes/freeport"
	"regexp"
)

var args = os.Args[1:]
var port = -1
var portSwitch = false
var cmd *exec.Cmd

func ExecuteArgs() int {
	freeport, err := freeport.GetFreePort()
	if err != nil {
		fmt.Printf("Failed to get port")
	}
	
	if len(args) > 1 {
		program := args[0]
		argv := args[1:]
		
		if portSwitch {
			port = freeport
			replaced := false
			// find numbers in arguments and replace with free port
			for i, s := range argv {
				if _, errA := strconv.Atoi(s); errA == nil {
					fmt.Printf("Changing port %q to %q for new container instance.\n", s, strconv.Itoa(port))
					argv[i] = strconv.Itoa(port)
				}
			}
			
			// attempt to replace in structures like --port=4433
			if replaced == false {
				for j, t := range argv {
					regex := regexp.MustCompile("[0-9]+")
					found := regex.FindAllString(t, -1)
					// ensure argument only has one number and ends with this number
					if len(found) == 1 && strings.HasSuffix(t, found[0]) && strings.Contains(t, "=") {
						fmt.Printf("Changing port %q of parameter %q to %q for new container instance.\n", found[0], t, strconv.Itoa(port))
						argv[j] = strings.Replace(argv[j], found[0], strconv.Itoa(port), -1)
					}
				}
			}
			
		} else {
			// try to set port according to arguments
			for _, s := range argv {
				if val, errA := strconv.Atoi(s); errA == nil {
					port = val
				}
			}
		}

		cmd = exec.Command(program, argv...)
	} else if len(args) > 0 {
		program := args[0]

		cmd = exec.Command(program)
	} else {
		fmt.Println("Nothing to do, no args specified")
		return -1
	}

	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	// keep stdin open
	_, err = cmd.StdinPipe()

	err = cmd.Run()

	if err != nil {
		fmt.Println(err)
	}
	return cmd.ProcessState.ExitCode()
}

func Shutdown(w http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(w, "shutdown...")
	fmt.Println("shutdown...")

	go os.Exit(0)
}

func Portrequest(w http.ResponseWriter, req *http.Request) {
	concatenated := strings.Join([]string{"Use:",strconv.Itoa(port), "-Port"}, "")
	fmt.Fprintf(w, concatenated)
	fmt.Println("Reported port")
}

func EnablePortSwitch(w http.ResponseWriter, req *http.Request) {
	portSwitch = true
	cmd.Process.Kill()
	fmt.Fprintf(w, "Port switching enabled, restarted server")
	fmt.Println("Enabled port switching, restarted server")
}

func KillServer(w http.ResponseWriter, req *http.Request) {
	cmd.Process.Kill()
	fmt.Fprintf(w, "The server is dead, long live the server")
	fmt.Println("Killed server as requested")
}
