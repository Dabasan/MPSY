package com.github.dabasan.mps;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.github.dabasan.ejml_3dtools.Vector;
import com.github.dabasan.jxm.bd1.BD1Manipulator;
import com.github.dabasan.jxm.pd1.PD1Manipulator;

/**
 * Main
 * 
 * @author Daba
 *
 */
public class MPSYMain {
	public static final String VERSION_STR = "MPSY v0.0.1";

	public static void main(String[] args) {
		// コマンドライン引数を解析するための設定
		var options = new Options();

		// 必須引数
		var optionBi = new Option("bi", "bd1FilepathIn", true, "Input filepath of a BD1 file");
		var optionPi = new Option("pi", "pd1FilepathIn", true, "Input filepath of a PD1 file");
		var optionBo = new Option("bo", "bd1FilepathOut", true, "Output filepath of a BD1 file");
		var optionPo = new Option("po", "pd1FilepathOut", true, "Output filepath of a PD1 file");
		// オプション引数
		var optionT = new Option("t", "translation", true, "Amount of translation (x, y, z)");
		var optionS = new Option("s", "scale", true, "Scale (x, y, z)");
		var optionRX = new Option("rx", "rotX", true,
				"Amount of rotation around the X-axis (radian)");
		var optionRY = new Option("ry", "rotY", true,
				"Amount of rotation around the Y-axis (radian)");
		var optionRZ = new Option("rz", "rotZ", true,
				"Amount of rotation around the Z-axis (radian)");
		var optionR = new Option("r", "rot", true,
				"Amount of rotation around an arbitrary axis (axisX, axisY, axisZ, angle (radian))");
		var optionZ = new Option("z", "invertZ", false, "Inverts Z-axis");
		var optionH = new Option("h", "help", false, "Help");
		var optionV = new Option("v", "version", false, "Version");

		optionT.setArgs(3);
		optionS.setArgs(3);
		optionR.setArgs(4);

		options.addOption(optionBi);
		options.addOption(optionPi);
		options.addOption(optionBo);
		options.addOption(optionPo);
		options.addOption(optionT);
		options.addOption(optionS);
		options.addOption(optionRX);
		options.addOption(optionRY);
		options.addOption(optionRZ);
		options.addOption(optionR);
		options.addOption(optionZ);
		options.addOption(optionH);
		options.addOption(optionV);

		// コマンドライン引数の解析
		var parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		// ヘルプの表示
		if (cmd.hasOption("h")) {
			var hf = new HelpFormatter();
			hf.printHelp("[options]", options);

			return;
		}
		// バージョン情報の表示
		if (cmd.hasOption("v")) {
			System.out.println(VERSION_STR);
			return;
		}

		// 必須引数がない場合にはエラー
		if (!cmd.hasOption("bi")) {
			System.err.println("Java-side error: Missing required option: bi");
			return;
		}
		if (!cmd.hasOption("pi")) {
			System.err.println("Java-side error: Missing required option: pi");
			return;
		}
		if (!cmd.hasOption("bo")) {
			System.err.println("Java-side error: Missing required option: bo");
			return;
		}
		if (!cmd.hasOption("po")) {
			System.err.println("Java-side error: Missing required option: po");
			return;
		}

		String bd1FilepathIn = cmd.getOptionValue("bi");
		String pd1FilepathIn = cmd.getOptionValue("pi");
		String bd1FilepathOut = cmd.getOptionValue("bo");
		String pd1FilepathOut = cmd.getOptionValue("po");
		String strTranslation = cmd.getOptionValue("t", "0.0 0.0 0.0");
		String strScale = cmd.getOptionValue("s", "1.0 1.0 1.0");
		String strRotX = cmd.getOptionValue("rx");
		String strRotY = cmd.getOptionValue("ry");
		String strRotZ = cmd.getOptionValue("rz");
		String strRot = cmd.getOptionValue("r", "1.0 1.0 1.0 0.0");
		boolean invertZ = cmd.hasOption("z");

		// 文字列を数値に変換する。
		String[] splitsTranslation = strTranslation.split(" ");
		var translation = new Vector(Double.parseDouble(splitsTranslation[0]),
				Double.parseDouble(splitsTranslation[1]), Double.parseDouble(splitsTranslation[2]));

		String[] splitsScale = strScale.split(" ");
		var scale = new Vector(Double.parseDouble(splitsScale[0]),
				Double.parseDouble(splitsScale[1]), Double.parseDouble(splitsScale[2]));

		double rotX = Double.parseDouble(strRotX);
		double rotY = Double.parseDouble(strRotY);
		double rotZ = Double.parseDouble(strRotZ);

		String[] splitsRot = strRot.split(" ");
		var rotAxis = new Vector(Double.parseDouble(splitsRot[0]), Double.parseDouble(splitsRot[1]),
				Double.parseDouble(splitsRot[2]));
		double rotAngle = Double.parseDouble(splitsRot[3]);

		// 実際にマップとポイントに処理を適用する。
		BD1Manipulator bd1Manipulator;
		PD1Manipulator pd1Manipulator;
		try {
			bd1Manipulator = new BD1Manipulator(bd1FilepathIn);
			pd1Manipulator = new PD1Manipulator(pd1FilepathIn);
		} catch (IOException e) {
			System.err.println("Java-side error: Failed to open file.");
			e.printStackTrace();

			return;
		}

		bd1Manipulator.translate(translation.getX(), translation.getY(), translation.getZ())
				.rescale(scale.getX(), scale.getY(), scale.getZ()).rotX(rotX).rotY(rotY).rotZ(rotZ)
				.rot(rotAxis.getX(), rotAxis.getY(), rotAxis.getZ(), rotAngle);
		pd1Manipulator.translate(translation.getX(), translation.getY(), translation.getZ())
				.rescale(scale.getX(), scale.getY(), scale.getZ()).rotX(rotX).rotY(rotY).rotZ(rotZ)
				.rot(rotAxis.getX(), rotAxis.getY(), rotAxis.getZ(), rotAngle);
		if (invertZ) {
			bd1Manipulator.invertZ();
			pd1Manipulator.invertZ();
		}

		bd1Manipulator.saveAsBD1(bd1FilepathOut);
		pd1Manipulator.saveAsPD1(pd1FilepathOut);
	}
}
