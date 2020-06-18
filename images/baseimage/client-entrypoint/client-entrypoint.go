package main

import (
	"bytes"
	"fmt"
	"net/http"
	"os"
	"os/exec"
)

var args = os.Args[1:]

func executeArgs() {
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
	if err := cmd.Run(); err != nil {
		fmt.Println(err.Error())
		fmt.Println(out.String())
		return
	}
	fmt.Print(out.String())
}

func trigger(w http.ResponseWriter, req *http.Request) {
	go executeArgs()
	fmt.Fprintf(w, "done")
}

func shutdown(w http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(w, "shutdown...")
	fmt.Println("shutdown...")

	go os.Exit(0)
}


func main() {
	go executeArgs()

	http.HandleFunc("/trigger", trigger)
	http.HandleFunc("/shutdown", shutdown)
	fmt.Println("Listening on :8090...")
	_ = http.ListenAndServe(":8090", nil)
}
