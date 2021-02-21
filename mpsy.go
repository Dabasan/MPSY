package main

import (
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
)

func main() {
	var err error

	//JREのディレクトリをPATHに追加する
	pathJRE := filepath.Join(".", "JRE", "bin")
	paths := os.Getenv("PATH")
	paths = pathJRE + ";" + paths

	err = os.Setenv("PATH", paths)
	if err != nil {
		panic(err)
	}

	//mpsy.jarを実行する
	args := make([]string, 2)
	args[0] = "-jar"
	args[1] = "mpsy.jar"
	args = append(args, os.Args...)

	cmd := exec.Command("java", args...)
	output, err := cmd.CombinedOutput()
	if err != nil {
		panic(err)
	}

	strOutput := string(output)
	if len(strOutput) != 0 {
		fmt.Print(strOutput)
	}
}
