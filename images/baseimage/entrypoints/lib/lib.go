package lib

import (
	"fmt"
	"net/http"
	"os"
	"os/exec"
)

var args = os.Args[1:]

func ExecuteArgs() int {
	var cmd *exec.Cmd
	if len(args) > 1 {
		program := args[0]
		argv := args[1:]

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
	_, err := cmd.StdinPipe()

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
