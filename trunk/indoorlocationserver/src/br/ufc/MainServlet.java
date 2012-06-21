package br.ufc;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Session;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import java.sql.DriverManager;
import br.ufc.entidades.Pessoa;


//No ondestroy da app android colocar para remover o usuário., chamar o metodo: removerUsuario
//no gerador de Id, colocar uma funcao hash para gerar os Ids, como o Guid do C#

public class MainServlet extends HttpServlet{

	HttpServletResponse responseTest = null;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		responseTest = resp;
		String querySelectAll = "select * from pessoa";
		Connection connection = null;     
		  String res;     
		  
		  try      
		{     
		    // Carregando o JDBC Driver     
		    String driverName = "org.gjt.mm.mysql.Driver"; // MySQL MM JDBC driver     
		        
		       Class.forName(driverName);     
		    
		    // Criando a conexão com o Banco de Dados     
		    String url = "jdbc:mysql://localhost:3307/silobocarvalho?autoReconnect=true";     
		    String username = "silobocarvalho";     
		    String password = "8454764";     
		    connection = (Connection) DriverManager.getConnection(url, username, password);     
		         
		} catch (ClassNotFoundException e)      
		{     
			resp.getWriter().append("ERRRO CLASS NOT FOUND");
		    
		} catch (SQLException e)      
		{     
		//Não está conseguindo se conectar ao banco      
			resp.getWriter().append("SQL ERRO");      
		}       
		        
		
		String id = req.getParameter("id");
		String nome = req.getParameter("nome");
		String acao = req.getParameter("acao");
		String local = req.getParameter("local");
		boolean adicionou = false;
		List<Pessoa> listPessoas = selectAllBd(connection, resp);
		
		if(acao != null){
		
		if(acao.equals("add"))
		{
			if(id != null && nome != null)
			{
				for(int i=0; i<listPessoas.size(); i++ )
				{
					if(listPessoas.get(i).getId() == Long.parseLong(id))
					{
						listPessoas.get(i).setLocal(local);
						adicionou = true;
						
						updateBanco(connection, listPessoas.get(i));
					}
				}
				if(adicionou == false )
				{
					Pessoa pessoa = new Pessoa();
					pessoa.setId(Long.parseLong(id));
					pessoa.setLocal(local);
					pessoa.setNome(nome);
					addBanco(connection, resp, pessoa);
				}
			}
		}else if(acao.equals("generate") && nome != null && local != null) //Generate Id
		{
			
			int idGenerated = generateId(connection, nome, local);
			
			resp.getWriter().append(String.valueOf(idGenerated));

		}
		}
		if((acao != null) &&  (!acao.equals("generate"))){
		for(int i=0; i<listPessoas.size(); i++){
		resp.getWriter().append("Id: " + listPessoas.get(i).getId() + "\nNome: " + listPessoas.get(i).getNome() + "\nLocal: " + listPessoas.get(i).getLocal() + "\n\n");
		}
		}
		try {
			connection.close();
		} catch (SQLException e) {
			resp.getWriter().append(e.toString());
		}
	}
	
	public List<Pessoa> selectAllBd(Connection connection, HttpServletResponse resp) throws IOException
	{
		Statement select = null;
		ResultSet r = null;
		List listPessoas = new ArrayList<Pessoa>();
		int i = 0;
		try {
			select = (Statement) connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			r = select.executeQuery("select * from pessoa");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		try {
			while (r.next()) {
			    int id = r.getInt("id");
			    String nome = r.getString("nome");
			    String local = r.getString("local");
			    
			    Pessoa p = new Pessoa();
			    p.setNome(nome);
			    p.setLocal(local);
			    p.setId(id);
			    
			    listPessoas.add(p);
			    
			    //resp.getWriter().append(id + nome + local);      
			}
		} catch (SQLException e) {
			
			resp.getWriter().append("Erro percorrer query result");     
			e.printStackTrace();
		}
		
		return listPessoas;
	}
	
	public void addBanco(Connection connection, HttpServletResponse resp, Pessoa pessoa) throws IOException
	{
	  	Statement select = null;
		int r;
		try {
			select = (Statement) connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			r = select.executeUpdate("insert into pessoa (nome, local) values ('" + pessoa.getNome() + "', '" + pessoa.getLocal() + "')");
		} catch (SQLException e) {
			resp.getWriter().append(e.toString());
			e.printStackTrace();
		}
	
	}
	
	public void updateBanco(Connection connection, Pessoa p) throws IOException
	{
		Statement select = null;
		int r;
		try {
			select = (Statement) connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			r = select.executeUpdate("UPDATE pessoa SET local = '" + p.getLocal() + "' WHERE id = " + p.getId());
		} catch (SQLException e) {
			responseTest.getWriter().append(e.toString());
			e.printStackTrace();
			
		}
	}
	
	public int generateId(Connection connection, String nome, String local) throws IOException
	{
		int id = -1;
		
		String query = "SELECT * FROM pessoa ORDER BY id DESC LIMIT 1";
		
		Statement select = null;
		ResultSet r = null;
		try {
			select = (Statement) connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			r = select.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		try {
			while (r.next()) {
			    id = r.getInt("id");
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		/*
		Pessoa p = new Pessoa();
		p.setId(id+1);
		p.setNome(nome);
		p.setLocal(local);
		*/
		//addBanco(connection, null, p);
		
		return id + 1;
	}
	
	public void removerUsuario(int id)
	{
		
	}
}
