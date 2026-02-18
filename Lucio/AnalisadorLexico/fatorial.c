#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define sim 1
#define nao 0

double Fatorial(double M);

double Fatorial(double M) {
    return ((M == 0) ? 1 : (M * Fatorial((M - 1))));
}


int main() {
    printf("%.2f\n", Fatorial());
    return 0;
}
