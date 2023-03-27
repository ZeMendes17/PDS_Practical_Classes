package src;

import java.util.ArrayList;

class Registos {
    // Data elements
    private ArrayList<Empregado>  empregados;
    // Stores the employees
    public  Registos() {
        empregados = new ArrayList<>();
    }

    public  void insere(Empregado emp) {
        empregados.add(emp);
    }
    
    public void remove( int codigo ) { 
        for(Empregado e : empregados) {
            if(e.codigo() == codigo)
                empregados.remove(e);
        }
    }

    public boolean isEmpregado( int codigo ) { 
        for(Empregado e : empregados)
            if(e.codigo() == codigo)
                return true;
        return false;
    }

    public List<Empregado> listaDeEmpregados () { 
        return empregados;
    }

}

