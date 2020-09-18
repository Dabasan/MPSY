package com.github.dabasan.mpsy;

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
	public static final String VERSION_STR = "MPSY v0.1.0";

	public static void main(String[] args) {
		// コマンドライン引数を解析するための設定
		var options = new Options();

		var optionBi = new Option("bi", "bd1FilepathIn", true, "Input filepath of a BD1 file");
		var optionPi = new Option("pi", "pd1FilepathIn", true, "Input filepath of a PD1 file");
		var optionBo = new Option("bo", "bd1FilepathOut", true, "Output filepath of a BD1 file");
		var optionPo = new Option("po", "pd1FilepathOut", true, "Output filepath of a PD1 file");
		var optionT = new Option("t", "translation", true, "Amount of translation [x, y, z]");
		var optionS = new Option("s", "scale", true, "Scale [x, y, z]");
		var optionRX = new Option("rx", "rotX", true, "Angle of rotation around the X-axis");
		var optionRY = new Option("ry", "rotY", true, "Angle of rotation around the Y-axis");
		var optionRZ = new Option("rz", "rotZ", true, "Angle of rotation around the Z-axis");
		var optionR = new Option("r", "rot", true,
				"Angle of rotation around an arbitrary axis [axisX, axisY, axisZ, angle]");
		var optionZ = new Option("z", "invertZ", false, "Inverts Z-axis");
		var optionH = new Option("h", "help", false, "Displays help");
		var optionV = new Option("v", "version", false, "Displays version info");

		optionT.setArgs(3);
		optionS.setArgs(3);
		optionR.setArgs(4);
		optionZ.setArgs(0);
		optionH.setArgs(0);
		optionV.setArgs(0);

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
			System.out.println("Specify angles in degree.");

			return;
		}
		// バージョン情報の表示
		if (cmd.hasOption("v")) {
			System.out.println(VERSION_STR);
			return;
		}

		boolean mBD1 = false;
		boolean mPD1 = false;
		if (cmd.hasOption("bi") && cmd.hasOption("bo")) {
			mBD1 = true;
		}
		if (cmd.hasOption("pi") && cmd.hasOption("po")) {
			mPD1 = true;
		}

		// BD1もPD1も操作しない場合にはプログラムを終了する。
		if (!mBD1 && !mPD1) {
			return;
		}

		String bd1FilepathIn = cmd.getOptionValue("bi");
		String pd1FilepathIn = cmd.getOptionValue("pi");
		String bd1FilepathOut = cmd.getOptionValue("bo");
		String pd1FilepathOut = cmd.getOptionValue("po");
		String[] strTranslation = cmd.getOptionValues("t");
		String[] strScale = cmd.getOptionValues("s");
		String strRotX = cmd.getOptionValue("rx");
		String strRotY = cmd.getOptionValue("ry");
		String strRotZ = cmd.getOptionValue("rz");
		String[] strRot = cmd.getOptionValues("r");
		boolean invertZ = cmd.hasOption("z");

		// 文字列を数値に変換する。
		var translation = new Vector();
		if (strTranslation != null) {
			translation.set(Double.parseDouble(strTranslation[0]),
					Double.parseDouble(strTranslation[1]), Double.parseDouble(strTranslation[2]));
		}

		var scale = new Vector(1.0, 1.0, 1.0);
		if (strScale != null) {
			scale.set(Double.parseDouble(strScale[0]), Double.parseDouble(strScale[1]),
					Double.parseDouble(strScale[2]));
		}

		double rotX = 0.0;
		double rotY = 0.0;
		double rotZ = 0.0;
		if (strRotX != null) {
			rotX = Double.parseDouble(strRotX);
			rotX = convertDegToRad(rotX);
		}
		if (strRotY != null) {
			rotY = Double.parseDouble(strRotY);
			rotY = convertDegToRad(rotY);
		}
		if (strRotZ != null) {
			rotZ = Double.parseDouble(strRotZ);
			rotZ = convertDegToRad(rotZ);
		}

		var rotAxis = new Vector(1.0, 1.0, 1.0);
		double rotAngle = 0.0;
		if (strRot != null) {
			rotAxis.set(Double.parseDouble(strRot[0]), Double.parseDouble(strRot[1]),
					Double.parseDouble(strRot[2]));
			rotAngle = Double.parseDouble(strRot[3]);

			rotAngle = convertDegToRad(rotAngle);
		}

		// 実際にマップとポイントに処理を適用する。
		BD1Manipulator bd1Manipulator = null;
		PD1Manipulator pd1Manipulator = null;
		try {
			if (mBD1) {
				bd1Manipulator = new BD1Manipulator(bd1FilepathIn);
			}
			if (mPD1) {
				pd1Manipulator = new PD1Manipulator(pd1FilepathIn);
			}
		} catch (IOException e) {
			System.err.println("Java-side error: Failed to open file.");
			e.printStackTrace();

			return;
		}

		if (mBD1) {
			bd1Manipulator.translate(translation.getX(), translation.getY(), translation.getZ())
					.rescale(scale.getX(), scale.getY(), scale.getZ()).rotX(rotX).rotY(rotY)
					.rotZ(rotZ).rot(rotAxis.getX(), rotAxis.getY(), rotAxis.getZ(), rotAngle);

			if (invertZ) {
				bd1Manipulator.invertZ();
			}

			bd1Manipulator.saveAsBD1(bd1FilepathOut);
		}
		if (mPD1) {
			pd1Manipulator.translate(translation.getX(), translation.getY(), translation.getZ())
					.rescale(scale.getX(), scale.getY(), scale.getZ()).rotX(rotX).rotY(rotY)
					.rotZ(rotZ).rot(rotAxis.getX(), rotAxis.getY(), rotAxis.getZ(), rotAngle);

			if (invertZ) {
				pd1Manipulator.invertZ();
			}

			pd1Manipulator.saveAsPD1(pd1FilepathOut);
		}
	}

	private static double convertDegToRad(double deg) {
		return Math.PI / 180.0 * deg;
	}
}
