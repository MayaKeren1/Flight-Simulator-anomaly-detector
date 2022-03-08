package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();
		// example: commands.add(c.new ExampleCommand());
		// implement
		commands.add(c.new Command1());
		commands.add(c.new Command2());
		commands.add(c.new Command3());
		commands.add(c.new Command4());
		commands.add(c.new Command5());

	}
	
	public void start() {
		// implement
		String option = dio.readText();
		while (!option.equals("6")) {
			dio.write("Welcome to the Anomaly Detection Server.\n");
			dio.write("Please choose an option:\n");
			dio.write("1. upload a time series csv file\n");
			dio.write("2. algorithm settings\n");
			dio.write("3. detect anomalies\n");
			dio.write("4. display results\n");
			dio.write("5. upload anomalies and analyze results\n");
			dio.write("6. exit\n");
			int val = Integer.parseInt(option);
			commands.get(val - 1).execute();
			option = dio.readText();
		}

	}
}
