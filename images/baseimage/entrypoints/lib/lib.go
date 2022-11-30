package lib

import (
	"fmt"
	"net/http"
	"strconv"
	"strings"
	"os"
	"os/exec"
	"github.com/phayes/freeport"
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
			for i, s := range argv {
				if _, errA := strconv.Atoi(s); errA == nil {
					fmt.Printf("Changing port %q to %q for new container instance.\n", s, strconv.Itoa(port))
					argv[i] = strconv.Itoa(port)
				}
			}
		} else {
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
