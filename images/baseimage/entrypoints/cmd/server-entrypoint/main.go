package main

import (
	"entrypoints/lib"
	"fmt"
	"net/http"
	"time"
)

func infinite() {
	for {
		start := time.Now()
		lib.ExecuteArgs()
		elapsed := time.Since(start)
		if elapsed < 50 * time.Millisecond {
			time.Sleep(500 * time.Millisecond)
		}
	}
}


func main() {
	go infinite()

	http.HandleFunc("/shutdown", lib.Shutdown)
	fmt.Println("Listening on :8090...")
	_ = http.ListenAndServe(":8090", nil)
}