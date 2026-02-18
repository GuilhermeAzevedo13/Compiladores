#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define sim 1
#define nao 0

double Fibonacci(double N);
double Principal(void);

double Fibonacci(double N) {
    return ((N == 0) ? 0 : ((N == 1) ? 1 : (Fibonacci((N - 1)) + Fibonacci((N - 2)))));
}

double Principal(void) {
    return Fibonacci(7);
}


int main() {
    printf("%.2f\n", Principal());
    return 0;
}
