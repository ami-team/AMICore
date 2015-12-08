package net.hep.ami.task;

public class ToyTask extends TaskAbstractClass
{
	/*---------------------------------------------------------------------*/

	@Override
	public void main(String argument) throws Exception
	{
		System.out.println("Hello World from '" + getTaskName() + "'!");
	}

	/*---------------------------------------------------------------------*/
}
