package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import java.util.function.LongConsumer;

public class Commands {

	// Default IO interface
	public interface DefaultIO {
		public String readText();

		public void write(String text);

		public float readVal();

		public void write(float val);

		// you may add default methods here
	}

	// the default IO to be used in all commands
	DefaultIO dio;

	public Commands(DefaultIO dio) {
		this.dio = dio;
	}

	// you may add other helper classes here


	// the shared state of all commands
	private class SharedState {
		// implement here whatever you need
		SimpleAnomalyDetector sad = new SimpleAnomalyDetector();
		TimeSeries test;
		List<AnomalyReport> error;
		public void DoTS(String filename)
		{

			test=new TimeSeries(filename);
		}

	}

	private SharedState sharedState = new SharedState();


	// Command abstract class
	public abstract class Command {
		protected String description;

		public Command(String description) {
			this.description = description;
		}

		public abstract void execute();
	}

	// Command class for example:
	public class ExampleCommand extends Command {

		public ExampleCommand() {
			super("this is an example of command");
		}

		@Override
		public void execute() {
			dio.write(description);
		}
	}

	// implement here all other commands
	public class Command1 extends Command {
		public Command1() {
			super("Please upload your local train CSV file");
		}

		@Override
		public void execute() {
			dio.write("Please upload your local train CSV file.\n");
			try {
				PrintWriter out;
				out = new PrintWriter(new FileWriter("anomalyTrain.csv"));
				String line = dio.readText();
				while (!line.equals("done")) {
					out.println(line);
					line = dio.readText();
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			dio.write("Upload complete.\n");

			dio.write("Please upload your local test CSV file.\n");
			try {
				PrintWriter out;
				out = new PrintWriter(new FileWriter("anomalyTest.csv"));
				String line = dio.readText();
				while (!line.equals("done")) {
					out.println(line);
					line = dio.readText();
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			dio.write("Upload complete.\n");

		}

	}

	public class Command2 extends Command {
		public Command2() {
			super("Please upload your local train CSV file");
		}

		@Override
		public void execute() {
			float number;
			dio.write("The current correlation threshold is 0.9\n");
			dio.write("Type a new threshold\n");
			number = Float.parseFloat(dio.readText());

			while (number < 0 || number > 1) {
				dio.write("please choose a value between 0 and 1.\n");
				dio.write("The current correlation threshold is 0.9\n");
				dio.write("Type a new threshold\n");
				number =Float.parseFloat(dio.readText());

			}
			sharedState.sad.threshold = number;
		}
	}

	public class Command3 extends Command {
		public Command3() {
			super("Please upload your local train CSV file");
		}

		@Override
		public void execute() {
			sharedState.sad.learnNormal(new TimeSeries("anomalyTrain.csv"));
			sharedState.error = sharedState.sad.detect(new TimeSeries("anomalyTest.csv"));
			dio.write("anomaly detection complete.\n");
		}

	}

	public class Command4 extends Command {
		public Command4() {
			super("Please upload your local train CSV file");
		}

		@Override
		public void execute() {
			for (int i = 0; i < sharedState.error.size(); i++) {
				dio.write(sharedState.error.get(i).timeStep);
				dio.write("	");
				dio.write(sharedState.error.get(i).description+ "\n");

			}
			dio.write("Done.\n");
		}
	}

	public class Command5 extends Command {
		public Command5() {
			super("Please upload your local train CSV file");
		}

		@Override
		public void execute() {
			float TP = 0;
			float FP = 0;
			List<long[]> contiue = new ArrayList<>();
			long arr[] = new long[2];
			String line = dio.readText();
			while (!line.equals("done")) {
				String[] values = line.split(",");
				arr[0] = Long.parseLong(values[0]);
				arr[1] = Long.parseLong(values[1]);
				contiue.add(arr);
				arr=new long [2];
				line = dio.readText();
			}
			dio.write("Please upload your local anomalies file.\n");
			dio.write("Upload complete\n");

			sharedState.DoTS("anomalyTest.csv");
			float Negative = sharedState.test.count;
			float Positive=0;
			List<long[]> contiueError = new ArrayList<>();
			long startindex = 0;
			long endindex = 1;
			boolean flag=false;
			for (int i = 0, index = i; i < sharedState.error.size() - 1; i++, index = i)
			{
				for (int j = i + 1; j < sharedState.error.size(); j++) {
					if (sharedState.error.get(i).timeStep + 1 == sharedState.error.get(j).timeStep && sharedState.error.get(i).description.equals(sharedState.error.get(j).description)) {
						startindex = sharedState.error.get(index).timeStep;
						flag = true;
						i++;
					}
					if (!flag) {
						break;
					}
					flag = false;
				}
				endindex= sharedState.error.get(i).timeStep;
				long[] arrError = {startindex, endindex};
				Negative-=endindex-startindex+1;
				contiueError.add(arrError);
			}

			Positive=contiue.size();

			boolean [] reported=new boolean[contiueError.size()];
			for(int i=0;i<contiueError.size();i++) {
				reported[i]=false;
			}

			for(int i=0;i<contiueError.size();i++) {
				for (int j = 0; j < contiue.size(); j++) {
					if (contiueError.get(i)[1] - contiue.get(j)[0] >= 0 && contiue.get(j)[1] - contiueError.get(i)[0] >= 0)
					{
						reported[i]=true;
					}
				}
			}


			for(int i=0;i<contiueError.size();i++) {
				if(reported[i]==false)
					FP++;
				else
					TP++;
			}

			DecimalFormat df = new DecimalFormat("#0.0");
			df.setMaximumFractionDigits(3);
			df.setRoundingMode(RoundingMode.DOWN);

			float TruePositve=TP/Positive;
			dio.write("True Positive Rate: ");
			dio.write(df.format(TruePositve));
			dio.write("\n");


			float FalsePostive=FP/Negative;
			dio.write("False Positive Rate: ");
			dio.write(df.format(FalsePostive));
			dio.write("\n");


		}
	}
}
