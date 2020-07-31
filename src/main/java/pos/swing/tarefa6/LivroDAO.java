package pos.swing.tarefa6;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    private final String stmtInserir = "INSERT INTO livro(titulo) VALUES(?)";
    private final String stmtConsultar = "SELECT * FROM livro WHERE id = ?";
    private final String stmtListaLivroAutor = "SELECT * FROM livro";
    private final String stmtListarAutoresLivro = "SELECT autor.nome "
			   									+ "FROM Livro_Autor "
			   									+ "INNER JOIN autor "
			   									+ "ON Livro_Autor.idAutor = autor.id "
			   									+ "WHERE Livro_Autor.idLivro = ?";
    //Método sem retorno para inserir livro. Recebe como argumento um objeto Livro.
    public void inserirLivro(Livro livro) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
        	//Cria a conexão com AutoCommit false
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);
            //Cria Statement com a capacidade de retornar keys geradas automaticamente
            stmt = con.prepareStatement(stmtInserir,PreparedStatement.RETURN_GENERATED_KEYS);
            //Dá ao primeiro parâmetro da query o título do livro
            stmt.setString(1, livro.getTitulo());
            stmt.executeUpdate();
            //Chama método lerIdLivro para retornar a ID
            int idLivroGravado = lerIdLivro(stmt);
            //Seta o id para objeto Livro
            livro.setId(idLivroGravado);
            //Chama método para gravar lista de autores na tabela Livro_Autor
            this.gravarAutores(livro, con);
            //Commita e fim
            con.commit();
        }
        catch (SQLException ex) {
            try{
            	con.rollback();
            }
            catch(SQLException ex1){
            	System.out.println("Erro ao tentar rollback. Ex="+ex1.getMessage());
            }
            throw new RuntimeException("Erro ao inserir um livro no banco de dados. Origem="+ex.getMessage());
        }
        finally{
            try{
            	stmt.close();
            }
            catch(Exception ex){
            	System.out.println("Erro ao fechar stmt. Ex="+ex.getMessage());
            }
            try{
            	con.close();
            }
            catch(Exception ex){
            	System.out.println("Erro ao fechar conexão. Ex="+ex.getMessage());
            }
        }
    }
     private int lerIdLivro(PreparedStatement stmt) throws SQLException {
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }   

    public Livro consultarLivro(int id) {
        Connection con=null;
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(stmtConsultar);
            Livro livroLido = null;
            stmt.setLong(1, id);
            rs = stmt.executeQuery();
            rs.next();
            List<Autor> listaAutores = lerAutores(id,con);
            livroLido = new Livro(rs.getString("titulo"), listaAutores);
            livroLido.setId(rs.getInt("Id"));
            return livroLido;           
        }catch(SQLException ex){
            throw new RuntimeException("Erro ao consultar um livro no banco de dados. Origem="+ex.getMessage());            
        }finally{
            try{rs.close();}catch(Exception ex){System.out.println("Erro ao fechar rs. Ex="+ex.getMessage());};
            try{stmt.close();}catch(Exception ex){System.out.println("Erro ao fechar stmt. Ex="+ex.getMessage());};
            try{con.close();;}catch(Exception ex){System.out.println("Erro ao fechar conexão. Ex="+ex.getMessage());};            
        }

    }
    //Método para gravar lista de autores para um livro na tabela Livro_Autor
    //Recebe objeto Livro com atributo Autores
    private void gravarAutores(Livro livro, Connection con) throws SQLException {
    	//Query stmt com dois parâmetros (id livro e id autor)
        String sql = "INSERT INTO Livro_Autor (idLivro, idAutor) VALUES ( ?, ?)";
        PreparedStatement stmt;
        stmt = con.prepareStatement(sql);
        //Seta a ID do livro para o primeiro argumento;
        stmt.setInt(1, livro.getId());
        //Pega lista de autores e faz loop para inserir dados
        List<Autor> autores = livro.getAutores();
        for (Autor autor : autores) {
            stmt.setLong(2, autor.getId());
            stmt.executeUpdate();
        }
    }

    private List<Autor> lerAutores(long idLivro, Connection con) throws SQLException{
        //Select para pegar os autores de um livro
        String sql = "SELECT autor.id,autor.nome"
                + " FROM autor"
                + " INNER JOIN Livro_Autor"
                + " ON autor.id = Livro_Autor.idAutor"
                + " WHERE Livro_Autor.idLivro = ? ";
        PreparedStatement stmt = null;
        List<Autor> autores = null;
        stmt = con.prepareStatement(sql);
        stmt.setLong(1, idLivro);
        ResultSet resultado = stmt.executeQuery();
        autores = new ArrayList<Autor>();
        while (resultado.next()) {
            Autor autorLido = new Autor(resultado.getString("nome"));
            autorLido.setId(resultado.getInt("id"));
            autores.add(autorLido);
        }

        return autores;
    }



    public List<Livro> listarLivroComAutores() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(stmtListaLivroAutor);            
            rs = stmt.executeQuery();
            List<Livro> listaLivros = new ArrayList<Livro>();
            while (rs.next()) {
                List<Autor> listAutores = lerAutores(rs.getInt(1),con);
                Livro livro = new Livro(rs.getString(2), listAutores);
                livro.setId(rs.getInt(1));
                listaLivros.add(livro);
            }

            return listaLivros;            
        }catch(SQLException ex){
            throw new RuntimeException("Erro ao listar um livro com autores no banco de dados. Origem="+ex.getMessage());            
        }finally{
            try{rs.close();}catch(Exception ex){System.out.println("Erro ao fechar rs. Ex="+ex.getMessage());};
            try{stmt.close();}catch(Exception ex){System.out.println("Erro ao fechar stmt. Ex="+ex.getMessage());};
            try{con.close();;}catch(Exception ex){System.out.println("Erro ao fechar conexão. Ex="+ex.getMessage());};                
        }
    }
    
    public List<String> listarAutoresLivro(int idLivro) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
    	List<String> listaAutor = new ArrayList();
    	
    	con = ConnectionFactory.getConnection();
    	stmt = con.prepareStatement(stmtListarAutoresLivro);
    	stmt.setInt(1, idLivro);
    	rs = stmt.executeQuery();
    	
    	while(rs.next()) {
    		listaAutor.add(rs.getString(1));
    	}
    	return listaAutor;
    }
    
}
