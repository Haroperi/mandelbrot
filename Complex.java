
public class Complex
{
	public double re, im;

	Complex(double re_, double im_)
	{
		re = re_;
		im = im_;
	}

	public double abs()
	{
		return re*re + im*im;
	}
}

