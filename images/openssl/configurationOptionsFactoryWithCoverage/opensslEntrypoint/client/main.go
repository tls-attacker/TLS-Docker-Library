package main

import (
	"context"
	"fmt"
	"net"
	"net/http"
	"os"
	"os/exec"
	"time"
)

var args = os.Args[1:]

var server *http.Server

var argv []string
var program string

var onShutdown bool = false

func Init() {
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
			"Missing Arguments for Client Entrypoint!\n" +
				"  Syntax is:   " + os.Args[0] + " <client program> <client program args>\n" +
				"  For Example: " + os.Args[0] + " openssl s_client -connect localhost:4433")
		os.Exit(1)
	}

}

func RunClientWithArgs() int {

	cmd := exec.Command(program, argv[:]...)

	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	err := cmd.Run()

	if err != nil {
		fmt.Println(err)
	}
	return cmd.ProcessState.ExitCode()
}

// Shutdowns the server after some time. Must be used in a parallel thread to properly shutdown after the shutdown request was sent.
// (When shutting down within the handler it terminates the running connection dirtyly . Sadly, the connection cannot be finalized manually in the handler (or so it seems...))
func ShutdownServer() {
	time.Sleep(200 * time.Millisecond)
	server.Shutdown(context.Background())
}

// Used to shutdown the server after /shutdown. The server is only terminated after the connection /shutdown request connection is closed properly.
func ConnStateListener(_ net.Conn, cs http.ConnState) {
	if cs == http.StateClosed && onShutdown {
		fmt.Println("Manager server shutdowned properly!")
		server.Shutdown(context.Background())
	}
}

func Shutdown(w http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(w, "shutdowned\n")
	fmt.Println("Shutdown function called.")
	onShutdown = true
}

func Trigger(w http.ResponseWriter, req *http.Request) {
	go RunClientWithArgs()
	fmt.Fprintf(w, "triggered")
}

func main() {
	Init()

	m := http.NewServeMux()
	server = &http.Server{Addr: ":8090", Handler: m, ConnState: ConnStateListener}

	m.HandleFunc("/shutdown", Shutdown)
	m.HandleFunc("/trigger", Trigger)

	fmt.Println("Listening on: 8090...")
	server.ListenAndServe()
}
