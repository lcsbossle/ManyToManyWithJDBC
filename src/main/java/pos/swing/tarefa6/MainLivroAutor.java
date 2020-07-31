package pos.swing.tarefa6;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class MainLivroAutor {
    
    private AutorDAO autorDAO;
    private LivroDAO livroDAO;

    public MainLivroAutor() throws Exception{
        autorDAO = new AutorDAO();
        livroDAO = new LivroDAO();
    }

    public static void main(String args[]) throws Exception{
        MainLivroAutor main = new MainLivroAutor();
        String opcao = "";
        while (true) {
            try{
            System.out.println("Escolha uma das opções e tecle <ENTER>: ");
            System.out.println("  1 - Incluir Autor");
            System.out.println("  2 - Incluir Livro");
            System.out.println("  3 - Listar Autores");
            System.out.println("  4 - Listar Livro com autores");
            System.out.println("  5 - Listar Autores de um livro");
            System.out.println("  6 - Listar Livros de um autor");
            System.out.println("  7 - Sair");
            Scanner sc = new Scanner(System.in,"ISO-8859-1");
            opcao = sc.nextLine();
            switch(opcao){
                case "1":
                    main.incluirAutor();
                    break;
                case "2":
                    main.incluirLivro();
                    break;
                case "3":
                    main.listarAutores();
                    break;
                case "4":
                    main.listarLivroComAutores();
                    break;
                case "5":
                	main.listarAutoresLivro();
                    break;
                case "6":
                	main.listarLivrosAutor();
                    break;
                default:
                    break;
            }
            if(opcao.equals("7")){
                break;
            }
            }catch(Exception ex){
                System.out.println("Falha na operação. Mensagem="+ ex.getMessage());
                //ex.printStackTrace();
            }
        }
    }
    public void incluirAutor() throws Exception{
    	//Pega da interface o título do autor
        System.out.print("Digite o nome do autor:");
        Scanner sc = new Scanner(System.in,"ISO-8859-1");
        String nome = sc.nextLine();
        
        int numLivros = 1;
        //Instancia lista de livros e inicia loop
        List<Livro> listaLivros = new ArrayList();
        int idLivro=0;
        //Loop recebe id de livro pela interface e adiciona à lista
        do{
            try{
            	//Pega da interface a ID do livro
                Scanner sc2 = new Scanner(System.in,"ISO-8859-1");
                System.out.print("ID Livro (0 para terminar) "+numLivros+":");
                idLivro = sc2.nextInt();
                //Se o usuário digitar -1 ele para o loop
                if(idLivro==0)
                    break;
                //Chama o método consultarLivro para verificar
                //se o mesmo existe a partir da ID fornecida
                Livro livro = livroDAO.consultarLivro(idLivro);
                //Se existir (não for null), adiciona à lista e aumenta o número de livros
                if(livro != null){
                	listaLivros.add(livro);
                    numLivros++;
                }else{
                    System.out.println("Livro não existe!");
                }
            }
            catch(Exception ex){
                System.out.println("\nID livro não é inteiro ou inválido!\n");
            }
        }while(true);
        //Instancia novo autor com nome e lista de livros
        Autor autor = new Autor(nome);
        autor.setLivros(listaLivros);
        //Adiciona à BD através da classe autorDAO
        autorDAO.inserirAutor(autor);
    }

    public void incluirLivro() {
    	//Pega da interface o título do livro
        System.out.print("Digite o título do livro:");
        Scanner sc = new Scanner(System.in,"ISO-8859-1");
        String titulo = sc.nextLine();
        
        int numAutores=1;
        //Instancia lista de autores e inicia loop
        List<Autor> listaAutores = new ArrayList();
        int idAutor=0;
        //Loop recebe id de autor pela interface e adiciona à lista
        do{
            try{
            	//Pega da interface a ID do autor
                Scanner sc2 = new Scanner(System.in,"ISO-8859-1");
                System.out.print("ID Autor "+numAutores+":");
                idAutor = sc2.nextInt();
                //Se o usuário digitar -1 ele para o loop
                if(idAutor==-1)
                    break;
                //Chama o método consultarAutor para verificar
                //se o mesmo existe a partir da ID fornecida
                Autor autor = autorDAO.consultarAutor(idAutor);
                //Se existir (não for null), adiciona à lista e aumenta o número de autores
                if(autor != null){
                    listaAutores.add(autor);
                    numAutores++;
                }else{
                    System.out.println("Autor não existe!");
                }
            }
            catch(Exception ex){
                System.out.println("\nID autor não é inteiro ou inválido!\n");
            }
        }while(true);
        //Instancia novo livro com título e lista de autores
        Livro livro = new Livro(titulo,listaAutores);
        //Adiciona à BD através da classe livroDAO
        livroDAO.inserirLivro(livro);
    }

    public void listarAutores() throws Exception{
        List<Autor> listaAutores = autorDAO.listarAutores();
        Collections.sort(listaAutores, new ComparadorAutor()); 
        System.out.println("ID\tNOME");
        for(Autor autor:listaAutores){
            System.out.println(autor.getId()+" \t"+autor.getNome());
        }
    }

    public void listarLivroComAutores() throws Exception{
        List<Livro> listaLivros = livroDAO.listarLivroComAutores();
        Collections.sort(listaLivros, new Comparator<Livro>() {
           public int compare(Livro arg0, Livro arg1) {
               String titulo = arg0.getTitulo();
               int i = titulo.compareToIgnoreCase(arg1.getTitulo());
               return i;
             }
        });
        System.out.println("ID\tTitulo do Livro\tAutores");
        for(Livro livro:listaLivros){
            System.out.print(livro.getId()+"\t"+livro.getTitulo()+"\t");
            for(Autor autor:livro.getAutores()){
                System.out.print(autor.getNome()+";");
            }
            System.out.print("\n");
        }

    }
    
    public void listarLivrosAutor() throws SQLException {
    	System.out.println("Insira o id do autor");
    	Scanner sc = new Scanner(System.in,"ISO-8859-1");
    	int idAutor = sc.nextInt();
    	
    	List<String> lista = autorDAO.listarLivrosAutor(idAutor);
    	for(String titulo:lista) {
    		System.out.println(titulo);
    	}
    	System.out.println("\n");
    }
    
    public void listarAutoresLivro() throws SQLException {
    	System.out.println("Insira o id do livro");
    	Scanner sc = new Scanner(System.in,"ISO-8859-1");
    	int idLivro = sc.nextInt();
    	
    	List<String> lista = livroDAO.listarAutoresLivro(idLivro);
    	for(String nome:lista) {
    		System.out.println(nome);
    	}
    	System.out.println("\n");
    }
    
    class ComparadorAutor implements Comparator<Autor>
    {

		@Override
		public int compare(Autor o1, Autor o2) {
			// TODO Auto-generated method stub
			return o1.getNome().compareTo(o2.getNome());
		}
    	
    }
    
}
