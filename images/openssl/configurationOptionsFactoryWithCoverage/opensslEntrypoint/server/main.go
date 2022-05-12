package main

import (
	"context"
	"fmt"
	"io"
	"net"
	"net/http"
	"os"
	"os/exec"
	"strconv"
	"time"
)

var args = os.Args[1:]

var onShutdown bool = false

var server *http.Server

var shutdownProgram string
var shutdownClientArgs [3]string

var stdin io.WriteCloser

var argv []string
var program string

func Init() {
	shutdownProgram = "openssl"
	shutdownClientArgs[0] = "s_client"
	shutdownClientArgs[1] = "-connect"
	shutdownClientArgs[2] = "localhost:4433"

	if len(args) > 0 {
		argv = make([]string, len(args)-1)
	} else {
		argv = make([]string, 0)
	}

	if len(args) > 1 {
		program = args[0]
		argv = args[1:]
	} else if len(args) > 0 {
		program = args[0]
	} else {
		fmt.Println(
			"Missing Arguments for Server Entrypoint!\n" +
				"  Syntax is:   " + os.Args[0] + " <server program> <server program args>\n" +
				"  For Example: " + os.Args[0] + " openssl s_server -accept 4433 -key /path/to/key -cert /path/to/cert")
		os.Exit(1)
	}
}

func StartServer() int {
	cmd := exec.Command(program, argv[:]...)

	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	// keep stdin open
	var err error
	stdin, _ = cmd.StdinPipe()

	err = cmd.Run()

	if err != nil {
		fmt.Println(err)
	}
	return cmd.ProcessState.ExitCode()
}

// Shutting down OpenSSL properly (without SIGKILL) is quite hard (and weird). At first we need to input the letter 'Q'. Afterwards the next client connection
// will trigger the shutdown. A proper shutdown is necessary for coverage data to be collected.
func Shutdown(w http.ResponseWriter, req *http.Request) {
	server.SetKeepAlivesEnabled(false)
	fmt.Fprintf(w, "shutdowned\n")
	fmt.Println("Shutdown function called.")

	onShutdown = true
	io.WriteString(stdin, "Q\n")
	cmd := exec.Command(shutdownProgram, shutdownClientArgs[:]...)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	cmd.Run()
	time.Sleep(200 * time.Millisecond)
	cmd.Process.Kill()
}

// Used to shutdown the server after /shutdown. The server is only terminated after the connection /shutdown request connection is closed properly.
func ConnStateListener(_ net.Conn, cs http.ConnState) {
	if cs == http.StateClosed && onShutdown {
		fmt.Println("Manager server shutdowned properly!")
		server.Shutdown(context.Background())
	}
}

func infinite() {
	failed := 0
	for {
		fmt.Println("Start Server!")
		start := time.Now()
		exitCode := StartServer()
		elapsed := time.Since(start)

		fmt.Println("Server terminated! (" + strconv.Itoa(int(elapsed.Milliseconds())) + "ms)")
		if elapsed < 100*time.Millisecond || exitCode > 0 || exitCode == -1 {
			time.Sleep(50 * time.Millisecond)
			failed = failed + 1
		} else {
			failed = 0
		}

		if failed > 5 {
			fmt.Println("Server crashed over 5 times. Manager stopped...")
			os.Exit(99)
		}

		if onShutdown {
			return
		}
	}
}

func main() {
	Init()
	m := http.NewServeMux()
	server = &http.Server{Addr: ":8090", Handler: m, ConnState: ConnStateListener}

	go infinite()
	m.HandleFunc("/shutdown", Shutdown)
	fmt.Println("Listening on: 8090...")
	server.ListenAndServe()
}
