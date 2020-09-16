package main

import (
	"flag"
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
)

func main() {
	var err error

	//JREのディレクトリ名を絶対パスに変換する。
	pathJRE, err := filepath.Abs(".\\jre\\bin")
	if err != nil {
		panic(err)
	}

	//JREのディレクトリをPATHに追加する。
	paths := os.Getenv("PATH")
	paths = pathJRE + ";" + paths

	err = os.Setenv("PATH", paths)
	if err != nil {
		panic(err)
	}

	//コマンドライン引数の解析を行う。
	flag.Parse()

	//mpsy.jarを実行する。
	args := make([]string, 2)
	args[0] = "-jar"
	args[1] = "mpsy.jar"
	args = append(args, flag.Args()...)

	cmd := exec.Command("java", args...)
	output, err := cmd.CombinedOutput()
	if err != nil {
		fmt.Println(fmt.Sprint(err) + ": " + string(output))
		return
	}

	fmt.Println(string(output))
}
