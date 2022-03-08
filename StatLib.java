package test;


public class StatLib {



	// simple average
	public static float avg(float[] x){
		float sum=0;
		for(float i:x)
			sum+=i;
		return sum/x.length;
	}

	// returns the variance of X and Y
	public static float var(float[] x){
		float avgX=avg(x);
		float y[]=new float[x.length];
		for(int i=0; i<x.length;i++)
		{
			y[i]=(float)Math.pow(x[i], 2);
		}

		return avg(y)-(float)Math.pow(avgX, 2);
	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		float z[]=new float [x.length];
		for (int i=0;i<x.length;i++)
		{
			z[i]=x[i]*y[i];
		}
		return avg(z)-(avg(x)*avg(y));
	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		return (cov(x,y)/(float)(Math.sqrt(var(x)*var(y))));
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){

		float x[]=new float [points.length];
		float y[]=new float [points.length];
		for(int i=0;i<x.length;i++)
		{
			x[i]=points[i].x;
			y[i]=points[i].y;
		}
		return linear_reg(x,y);

	}

	public static Line linear_reg(float[] x,float[] y)
	{
		float A,B;
		A=cov(x,y)/var(x);
		B=avg(y)-(A*avg(x));
		Line line=new Line(A,B);
		return line;
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		Line line=linear_reg(points);
		return dev(p,line);
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		return Math.abs(l.f(p.x)-p.y);
	}

}
