#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define sim 1
#define nao 0

int ExisteTriangulo(double LadoA, double LadoB, double LadoC);
double TesteOperadores(double A, double B);
int Principal(void);

int ExisteTriangulo(double LadoA, double LadoB, double LadoC) {
    return (((((LadoA + LadoB) > LadoC) && ((LadoA + LadoC) > LadoB)) && ((LadoB + LadoC) > LadoA)) ? 1 : 0);
}

double TesteOperadores(double A, double B) {
    return ((A < B) ? (A * 10) : ((int)B % (int)3));
}

int Principal(void) {
    double LadoUm = 10.5;
    double LadoDois = 20.0;
    double LadoTres = 25.2;
    return ExisteTriangulo(LadoUm, LadoDois, LadoTres);
}


int main() {

    printf("Resultado: %d\n", Principal());
    return 0;
}
