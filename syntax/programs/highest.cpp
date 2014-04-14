int main()
{
	int n1, n2, n3, h;
	n1 = 21;
	n2 = 23;
	n3 = 32;

	if (n1 > n2)
		if (n1 > n3)
			h = n1;
		else
			h = n3;
	else if (n2 > n3)
		h = n2;
	else
		h = n3;
}