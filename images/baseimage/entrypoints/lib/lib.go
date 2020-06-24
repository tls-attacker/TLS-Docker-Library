package lib

import (
	"bytes"
	"fmt"
	"net/http"
	"os"
	"os/exec"
)

var args = os.Args[1:]

func ExecuteArgs() {
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
		return
	}

	var out bytes.Buffer
	cmd.Stdout = &out
	cmd.Stderr = &out
	err := cmd.Run()

	fmt.Print(out.String())
	if err != nil {
		fmt.Println(err)
	}
}

func Shutdown(w http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(w, "shutdown...")
	fmt.Println("shutdown...")

	go os.Exit(0)
}
