package main

import (
	"fmt"
	"net/http"
	"os"
	"strconv"
	"time"
	"os/exec"
	"io"
)

var args = os.Args[1:]

var onShutdown bool = false

var sClientArgs [3]string

var stdin io.WriteCloser

func initFunc() {
	sClientArgs[0] = "s_client"
	sClientArgs[1] = "-connect"
	sClientArgs[2] = "localhost:4433"
}

func StartServer() int {
	var cmd *exec.Cmd
	if len(args) > 1 {
		program := args[0]
		argv := args[1:]

		cmd = exec.Command(program, argv...)
	} else if len(args) > 0 {
		program := args[0]

		cmd = exec.Command(program)
	} else {
		// For debug
		var argv[7]string

		program := "openssl"
		argv[0] = "s_server"
		argv[1] = "-accept"
		argv[2] = "4433"
		argv[3] = "-key"
		argv[4] = "/home/fabian/OtherRepos/Builds/ssl/ec256key.pem"
		argv[5] = "-cert"
		argv[6] = "/home/fabian/OtherRepos/Builds/ssl/ec256cert.pem"

		cmd = exec.Command(program, argv[:]...)

		//fmt.Println("Nothing to do, no args specified")
		//return -1
	}

	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	// keep stdin open
	var err error
	stdin, err = cmd.StdinPipe()

	err = cmd.Run()

	if err != nil {
		fmt.Println(err)
	}
	return cmd.ProcessState.ExitCode()
}

// Shutting down OpenSSL properly (without SIGKILL) is quite hard (and weird). At first we need to input the letter 'Q'. Afterwards the next client connection
// will trigger the shutdown. A proper shutdown is necessary for coverage data to be collected. 
func Shutdown(w http.ResponseWriter, req *http.Request) {

	fmt.Println("shutdown function called")

	io.WriteString(stdin, "Q\n")

	onShutdown = true
	var program string
	if len(args) == 0 {
		program = "openssl"
	} else {
		program = args[0]
	}
	

	cmd := exec.Command(program, sClientArgs[:]...)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	cmd.Run()

	fmt.Fprintf(w, "shutdown...")
	fmt.Println("shutdown...")

}

func infinite() {
	failed := 0
	for {
		fmt.Println("Start Server!")
		start := time.Now()
		exitCode := StartServer()
		elapsed := time.Since(start)

		fmt.Println("Server terminated! (" + strconv.Itoa(int(elapsed.Milliseconds())) + "ms)")
		if elapsed < 100 * time.Millisecond || exitCode > 0 || exitCode == -1 {
			time.Sleep(50 * time.Millisecond)
			failed = failed + 1
		} else {
			failed = 0
		}

		if failed > 5 {
			os.Exit(99)
		}

		if onShutdown == true {
			fmt.Println("Server shutdowned properly.")
			os.Exit(0)
		}
	}
}

func main() {
	initFunc()

	go infinite()

	http.HandleFunc("/shutdown", Shutdown)
	fmt.Println("Listening on :8090...")
	_ = http.ListenAndServe(":8090", nil)
}