package com.github.dabasan.mpsy;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.joml.Vector3f;

import com.github.dabasan.jxm.bd1.BD1Manipulator;
import com.github.dabasan.jxm.pd1.PD1Manipulator;

/**
 * Main
 * 
 * @author Daba
 *
 */
public class MPSYMain {
	public static final String VERSION_STR = "MPSY v1.0.0-rc1";

	public static void main(String[] args) {
		// コマンドライン引数を解析するための設定
		var options = new Options();

		var optionBi = new Option("bi", "bd1FilepathIn", true, "Input filepath of a BD1 file");
		var optionPi = new Option("pi", "pd1FilepathIn", true, "Input filepath of a PD1 file");
		var optionBo = new Option("bo", "bd1FilepathOut", true, "Output filepath of a BD1 file");
		var optionPo = new Option("po", "pd1FilepathOut", true, "Output filepath of a PD1 file");
		var optionT = new Option("t", "translation", true, "Amount of translation [x, y, z]");
		var optionS = new Option("s", "scale", true, "Scale [x, y, z]");
		var optionRX = new Option("rx", "rotX", true, "Amount of rotation around the X-axis");
		var optionRY = new Option("ry", "rotY", true, "Amount of rotation around the Y-axis");
		var optionRZ = new Option("rz", "rotZ", true, "Amount of rotation around the Z-axis");
		var optionR = new Option("r", "rot", true,
				"Amount of rotation around an arbitrary axis [amount, axisX, axisY, axisZ]");
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
			System.out.println("Specify amount of rotation in degree.");

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

		// BD1もPD1も操作しない場合にはプログラムを終了する
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

		// 文字列を数値に変換する
		var translation = new Vector3f();
		if (strTranslation != null) {
			translation.x = Float.parseFloat(strTranslation[0]);
			translation.y = Float.parseFloat(strTranslation[1]);
			translation.z = Float.parseFloat(strTranslation[2]);
		}

		var scale = new Vector3f(1.0f, 1.0f, 1.0f);
		if (strScale != null) {
			scale.x = Float.parseFloat(strScale[0]);
			scale.y = Float.parseFloat(strScale[1]);
			scale.z = Float.parseFloat(strScale[2]);
		}

		float rotX = 0.0f;
		float rotY = 0.0f;
		float rotZ = 0.0f;
		if (strRotX != null) {
			rotX = Float.parseFloat(strRotX);
			rotX = (float) Math.toRadians(rotX);
		}
		if (strRotY != null) {
			rotY = Float.parseFloat(strRotY);
			rotY = (float) Math.toRadians(rotY);
		}
		if (strRotZ != null) {
			rotZ = Float.parseFloat(strRotZ);
			rotZ = (float) Math.toRadians(rotZ);
		}

		var rotAxis = new Vector3f(1.0f, 1.0f, 1.0f);
		float rotAngle = 0.0f;
		if (strRot != null) {
			rotAngle = (float) Math.toRadians(Float.parseFloat(strRot[0]));
			rotAxis.x = Float.parseFloat(strRot[1]);
			rotAxis.y = Float.parseFloat(strRot[2]);
			rotAxis.z = Float.parseFloat(strRot[3]);
		}

		// 実際にマップとポイントに処理を適用する
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
			bd1Manipulator.translate(translation.x, translation.y, translation.z)
					.rescale(scale.x, scale.y, scale.z).rotX(rotX).rotY(rotY).rotZ(rotZ)
					.rot(rotAngle, rotAxis.x, rotAxis.y, rotAxis.z);

			if (invertZ) {
				bd1Manipulator.invertZ();
			}

			bd1Manipulator.saveAsBD1(bd1FilepathOut);
		}
		if (mPD1) {
			pd1Manipulator.translate(translation.x, translation.y, translation.z)
					.rescale(scale.x, scale.y, scale.z).rotX(rotX).rotY(rotY).rotZ(rotZ)
					.rot(rotAngle, rotAxis.x, rotAxis.y, rotAxis.z);

			if (invertZ) {
				pd1Manipulator.invertZ();
			}

			pd1Manipulator.saveAsPD1(pd1FilepathOut);
		}
	}
}
