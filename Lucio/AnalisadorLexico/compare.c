#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define sim 1
#define nao 0

int Compare(double A, double B, double C);
int Aplica(int (*F)(double, double, double), double A, double B, double C);
int Principal(void);

int Compare(double A, double B, double C) {
    return ((!(A == B) || !(B == C)) ? 0 : 1);
}

int Aplica(int (*F)(double, double, double), double A, double B, double C) {
    return F(A, B, C);
}

int Principal(void) {
    return Aplica(Compare, 2, 1, 3);
}


int main() {

    printf("Resultado: %d\n", Principal());
    return 0;
}
