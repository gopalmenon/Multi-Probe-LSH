#include <stdio.h>
#include <stdlib.h>
#include <time.h>

static int DIMENSION = 4;
static int DATA_SIZE = 1000;

int main(int argc, char** args)
{
  if (argc != 3)
  {
    fprintf(stderr, "Need to input desired dimension and data_size\n");
    return 1;
  }

  DIMENSION = atoi(args[1]);
  DATA_SIZE  = atoi(args[2]);

  double random_value;

  srand ( time ( NULL));

  int i, j;
  for (i = 0; i < DATA_SIZE; i++)
    {
      for (j = 0; j < DIMENSION; j++)
	{
	  random_value = (double)rand()/RAND_MAX*2.0-1.0;//float in range -1 to 1
	  if (j != DIMENSION - 1 || (j != DIMENSION - 1 && i != DATA_SIZE - 1))
	    printf ( "%f ", random_value);
	  else
	    printf ( "%f\n", random_value);
	}
    }
  return 0;
}
