package main

import (
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
)

func main() {
	var err error

	//JREのディレクトリ名を絶対パスに変換する
	relPathJRE := filepath.Join(".", "jre", "bin")
	absPathJRE, err := filepath.Abs(relPathJRE)
	if err != nil {
		panic(err)
	}

	//JREのディレクトリをPATHに追加する
	paths := os.Getenv("PATH")
	paths = absPathJRE + ";" + paths

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
	strOutput := string(output)
	if err != nil {
		fmt.Fprintf(os.Stderr, "%v: %v", err, strOutput)
		return
	}

	if len(strOutput) != 0 {
		fmt.Print(strOutput)
	}
}
