#include <iostream>
using namespace std;

int main(){
	for(int linha = 0; linha < 100; linha++){
		for(int byte = 0; byte < 100; byte++){
			cout << (char)linha;
		}
	}
}
