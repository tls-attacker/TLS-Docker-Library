package main

import (
	"entrypoints/lib"
	"fmt"
	"net/http"
)


func trigger(w http.ResponseWriter, req *http.Request) {
	go lib.ExecuteArgs()
	fmt.Fprintf(w, "done")
}


func main() {
	go lib.ExecuteArgs()

	http.HandleFunc("/trigger", trigger)
	http.HandleFunc("/shutdown", lib.Shutdown)
	fmt.Println("Listening on :8090...")
	_ = http.ListenAndServe(":8090", nil)
}
