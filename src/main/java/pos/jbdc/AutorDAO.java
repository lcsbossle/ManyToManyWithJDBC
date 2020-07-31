package pos.jbdc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {

    private final String stmtInserir = "INSERT INTO autor(nome) VALUES(?)";
    private final String stmtConsultar = "SELECT * FROM autor WHERE id = ?";
    private final String stmtListar = "SELECT * FROM autor";
    private final String stmtListarLivrosAutor = "SELECT livro.titulo "
    										   + "FROM Livro_Autor "
    										   + "INNER JOIN livro "
    										   + "ON Livro_Autor.idLivro = livro.id "
    										   + "WHERE Livro_Autor.idAutor = ?";
    private final String stmtListarAutoresLivro = "SELECT idAutor FROM Livro_Autor WHERE idLivro = ?";
    
    
    //Método sem retorno para inserir autor no BD. Recebe objeto Autor como argumento
    public void inserirAutor(Autor autor) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
        	//Cria a conexão com o BD
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);
            //Cria statement com capacidade de retornar chaves automátcas
            stmt = con.prepareStatement(stmtInserir,PreparedStatement.RETURN_GENERATED_KEYS);
            //Atribui o nome do autor ao primeiro argumento da query
            stmt.setString(1, autor.getNome());
            stmt.executeUpdate();
            //Chama método lerIdAutor para retornar a ID
            int idAutorGravado = lerIdAutor(stmt);
            //Seta o id para o objeto Autor
            autor.setId(idAutorGravado);
            //Chama método para gravar lista de livros na tabela Livro_Autor
            this.gravarLivros(autor, con);
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
    
    private int lerIdAutor(PreparedStatement stmt) throws SQLException {
        ResultSet rs = stmt.getGeneratedKeys();
        rs.next();
        return rs.getInt(1);
    }

    public Autor consultarAutor(int id) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Autor autorLido;
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(stmtConsultar);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if(rs.next()){
                autorLido = new Autor(rs.getString("nome"));
                autorLido.setId(rs.getInt("id"));
                return autorLido;
            }else{
                throw new RuntimeException("Não existe autor com este id. Id="+id);
            }
            
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao consultar um autor no banco de dados. Origem="+ex.getMessage());
        } finally{
            try{rs.close();}catch(Exception ex){System.out.println("Erro ao fechar result set. Ex="+ex.getMessage());};
            try{stmt.close();}catch(Exception ex){System.out.println("Erro ao fechar stmt. Ex="+ex.getMessage());};
            try{con.close();;}catch(Exception ex){System.out.println("Erro ao fechar conexão. Ex="+ex.getMessage());};
        }

    }
    //Método para gravar lista de livros para um autor na tabela Livro_Autor
    //Recebe objeto Autor com atributo Livros
    private void gravarLivros(Autor autor, Connection con) throws SQLException {
    	//Query com dois parâmetros (id livro e id argumento)
    	String sql = "INSERT INTO Livro_Autor (idLivro, idAutor) VALUES ( ?, ?)";
        PreparedStatement stmt;
        stmt = con.prepareStatement(sql);
        //Seta a ID do autor para o segundo argumento
        stmt.setInt(2, autor.getId());
        //Pega listade livros e faz loop para inserir dados
        List<Livro> livros = autor.getLivros();
        for (Livro livro : livros) {
        	stmt.setLong(1, livro.getId());
        	stmt.executeUpdate();
        }
    }

    public List<Autor> listarAutores() throws Exception {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Autor> lista = new ArrayList();
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(stmtListar);
            rs = stmt.executeQuery();
            while(rs.next()){
                Autor autor = new Autor(rs.getString("nome"));
                autor.setId(rs.getInt("id"));
                lista.add(autor);
            }
            return lista;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao consultar uma lista de autores. Origem="+ex.getMessage());
        }finally{
            try{rs.close();}catch(Exception ex){System.out.println("Erro ao fechar result set. Ex="+ex.getMessage());};
            try{stmt.close();}catch(Exception ex){System.out.println("Erro ao fechar stmt. Ex="+ex.getMessage());};
            try{con.close();;}catch(Exception ex){System.out.println("Erro ao fechar conexão. Ex="+ex.getMessage());};               
        }

    }
    
    public List<String> listarLivrosAutor(int idAutor) throws SQLException {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
    	List<String> listaTitulo = new ArrayList();
    	
    	con = ConnectionFactory.getConnection();
    	stmt = con.prepareStatement(stmtListarLivrosAutor);
    	stmt.setInt(1, idAutor);
    	rs = stmt.executeQuery();
    	
    	while(rs.next()) {
    		listaTitulo.add(rs.getString(1));
    	}
    	return listaTitulo;
    }
}

//String name="sonoo";  
//String sf1=String.format("name is %s",name);  



