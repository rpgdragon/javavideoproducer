package javavideoproducer;

public class TestPrueba {

	public static void main(String[] args) {
		String nombre_cancion="Perico de los palotes choricea";
		String comprobador = ".*";
		
		System.out.println(nombre_cancion.matches(comprobador));
	}
}
