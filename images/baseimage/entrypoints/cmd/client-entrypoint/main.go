package main

import (
	"entrypoints/lib"
	"fmt"
	"net/http"
	"os"
	"time"
)

func trigger(w http.ResponseWriter, req *http.Request) {
	go lib.ExecuteArgs()
	fmt.Fprintf(w, "done")
}

func main() {
	if len(os.Args) > 1 {
		go lib.ExecuteArgs()

		http.HandleFunc("/trigger", trigger)
		http.HandleFunc("/shutdown", lib.Shutdown)
		fmt.Println("Listening on :8090...")
		_ = http.ListenAndServe(":8090", nil)
	} else {
		fmt.Println("No args specified; sleeping forever just to keep the container alive")
		// Using a fancier sleep (as in https://stackoverflow.com/a/36419222/) causes go to die,
		// as all routines are sleeping, which is interpreted as a deadlock
		// So we just sleep for an hour forever
		for true {
			time.Sleep(time.Hour)
		}
	}
}
