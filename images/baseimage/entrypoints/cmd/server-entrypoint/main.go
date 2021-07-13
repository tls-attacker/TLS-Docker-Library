package main

import (
	"entrypoints/lib"
	"fmt"
	"net/http"
	"os"
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
		if elapsed < 100 * time.Millisecond || exitCode > 0 || exitCode == -1 {
			time.Sleep(50 * time.Millisecond)
			failed = failed + 1
		} else {
			failed = 0
		}

		if failed > 5 {
			os.Exit(99)
		}
	}
}


func main() {
	go infinite()

	http.HandleFunc("/shutdown", lib.Shutdown)
	fmt.Println("Listening on :8090...")
	_ = http.ListenAndServe(":8090", nil)
}