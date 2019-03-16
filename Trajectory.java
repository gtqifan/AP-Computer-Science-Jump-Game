public class Trajectory
{
	public double a, b, height, coefficient, distance;
	
	public void newTraj(double hForceLength, double vForceLength, int playerHeight)
	{
		a = hForceLength * 3.58974;
		b = vForceLength * 1.48718;
		height = playerHeight;
		coefficient = b / (((-a / 2) - 180) * ((-a / 2) - 180));
	}
	
	public int getX(int index) //index is between 0 to 50, around a second
	{
		distance = index * a / 200; 
		
		return (int) (2 * distance + 180);
	}
	
	public int getY(int index)
	{
		int y;
		
		distance = index * a / 50; 
		y = (int) (coefficient * (distance - 180 - (a / 2)) * (distance - 180 - (a / 2)) + height - b);
		
		return y;
	}
	
}
