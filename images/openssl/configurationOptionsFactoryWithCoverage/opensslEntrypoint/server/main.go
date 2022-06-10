package main

import (
	"context"
	"fmt"
	"io"
	"net"
	"net/http"
	"os"
	"os/exec"
	"os/signal"
	"strconv"
	"syscall"
	"time"
)

var args = os.Args[1:]

var onShutdown bool = false

var server *http.Server

var shutdownProgram string
var shutdownClientArgs [3]string
var state string

var stdin io.WriteCloser

var argv []string
var program string

func Init() {
	state = "initial"
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
		debug := true
		if debug {
			program = "openssl"
			argv = make([]string, 7)
			argv[0] = "s_server"
			argv[1] = "-accept"
			argv[2] = "4433"
			argv[3] = "-key"
			argv[4] = "/home/fabian/OtherRepos/Builds/ssl/ec256key.pem"
			argv[5] = "-cert"
			argv[6] = "/home/fabian/OtherRepos/Builds/ssl/ec256cert.pem"
			fmt.Printf("PID: %d\n", os.Getpid())

			return
		}

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
	state = "running"
	err = cmd.Run()

	if err != nil {
		fmt.Println(err)
		state = "error"
	}
	return cmd.ProcessState.ExitCode()
}

// Shutting down OpenSSL properly (without SIGKILL) is quite hard (and weird). At first we need to input the letter 'Q'. Afterwards the next client connection
// will trigger the shutdown. A proper shutdown is necessary for coverage data to be collected.
func StopServer() {
	io.WriteString(stdin, "Q\n")
	cmd := exec.Command(shutdownProgram, shutdownClientArgs[:]...)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	cmd.Run()
	time.Sleep(200 * time.Millisecond)
	cmd.Process.Kill()
}

func Shutdown(w http.ResponseWriter, req *http.Request) {
	server.SetKeepAlivesEnabled(false)
	fmt.Fprintf(w, "shutdowned\n")
	fmt.Println("Shutdown function called.")

	onShutdown = true
	StopServer()
}

func State(w http.ResponseWriter, _ *http.Request) {
	fmt.Fprint(w, state)
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
			state = "error"
			fmt.Println("Server crashed over 5 times. Manager stopped...")
			os.Exit(99)
		}

		if onShutdown {
			return
		}
	}
}

func ConfigureProperTermination() {
	signalChanel := make(chan os.Signal, 1)
	signal.Notify(signalChanel, syscall.SIGTERM)

	go func() {
		for {
			s := <-signalChanel
			switch s {
			case syscall.SIGTERM:
				fmt.Println("Termination signal triggered.")
				onShutdown = true
				StopServer()
				server.Shutdown(context.Background())

			default:
				fmt.Println("Unknown signal triggered.")
				os.Exit(98)
			}
		}
	}()
}

func main() {
	Init()
	ConfigureProperTermination()

	m := http.NewServeMux()
	server = &http.Server{Addr: ":8090", Handler: m, ConnState: ConnStateListener}

	go infinite()
	m.HandleFunc("/shutdown", Shutdown)
	m.HandleFunc("/state", State)
	fmt.Println("Listening on: 8090...")
	server.ListenAndServe()
}
