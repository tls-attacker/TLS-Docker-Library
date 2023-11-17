package main

import (
	"entrypoints/lib"
	"fmt"
	"net/http"
	"strconv"
	"time"
)

func infinite() {
	failed := 0
	for {
		fmt.Println("Start Server!")
		start := time.Now()
		exitCode := lib.ExecuteArgs()
		elapsed := time.Since(start)

		fmt.Println("Server terminated! (" + strconv.Itoa(int(elapsed.Milliseconds())) + "ms)")
		if elapsed < 20 * time.Millisecond || exitCode > 0 || exitCode == -1 {
			failed = failed + 1
		} else {
			failed = 0
		}
	}
}


func main() {
	go infinite()

	http.HandleFunc("/shutdown", lib.Shutdown)
	http.HandleFunc("/portrequest", lib.Portrequest)
	http.HandleFunc("/enableportswitch", lib.EnablePortSwitch)
	http.HandleFunc("/killserver", lib.KillServer)
	fmt.Println("Listening on :8090...")
	_ = http.ListenAndServe(":8090", nil)
}
