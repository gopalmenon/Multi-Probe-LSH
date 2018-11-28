#include <stdio.h>
#include <stdlib.h>
#include <time.h>

static const int DIMENSION = 4;
static const int DATA_SIZE = 1000;

int main()
{
  double random_value;

  srand ( time ( NULL));

  int i, j;
  for (i = 0; i < DATA_SIZE; i++)
  {
    for (j = 0; j < DIMENSION; j++)
    {
    random_value = (double)rand()/RAND_MAX*2.0-1.0;//float in range -1 to 1
    if (j != DIMENSION-1 )
      printf ( "%f ", random_value);
    else
      printf ( "%f\n", random_value);
    }
  }
  return 0;
}