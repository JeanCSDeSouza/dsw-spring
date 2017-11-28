package br.unirio.dsw.selecaoppgi.service.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import br.unirio.dsw.selecaoppgi.model.usuario.PapelUsuario;
import br.unirio.dsw.selecaoppgi.model.usuario.Usuario;
import br.unirio.dsw.selecaoppgi.utils.DateUtils;
import lombok.Getter;

/**
 * Classe responsavel pela persistencia de usuários
 * 
 * @author Marcio Barros
 */
@Service("userDAO")
public class UsuarioDAO extends AbstractDAO
{
	/**
	 * Carrega os dados de um usuário a partir do resultado de uma consulta
	 */
	private Usuario carrega(ResultSet rs) throws SQLException
	{
		String name = rs.getString("nome");
		String email = rs.getString("email");
		String password = rs.getString("senha");
		PapelUsuario papel = PapelUsuario.get(rs.getInt("papel"));
		boolean locked = rs.getInt("forcaResetSenha") != 0;

		Usuario user = new Usuario(name, email, password, papel, locked);
		user.setId(rs.getInt("id"));
		user.setPapel(PapelUsuario.get(rs.getInt("papel")));
		user.setDataUltimoLogin(DateUtils.toDateTime(rs.getTimestamp("dataUltimoLogin")));
		user.setContadorLoginFalhas(rs.getInt("contadorLoginFalha"));
		user.setTokenLogin(rs.getString("tokenLogin"));
		user.setDataTokenLogin(DateUtils.toDateTime(rs.getTimestamp("dataTokenLogin")));
		user.setIdEdital(rs.getInt("idEditalSelecionado"));
		return user;
	}

	/**
	 * Carrega um usuário, dado seu identificador
	 */
	public Usuario carregaUsuarioId(int id)
	{
		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		try
		{
			PreparedStatement ps = c.prepareStatement("SELECT * FROM Usuario WHERE id = ?");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			Usuario item = rs.next() ? carrega(rs) : null;
			c.close();
			return item;

		} catch (SQLException e)
		{
			log("UserDAO.carregaUsuarioId: " + e.getMessage());
			return null;
		}
	}
	/**
	 * Lista usuarios com role 2, vulgo professores.
	 * @return
	 */
	public List<Usuario> listaProfessores(){
		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		List<Usuario> professores = new ArrayList<Usuario>();
		try
		{
			PreparedStatement ps = c.prepareStatement("SELECT * FROM Usuario WHERE papel = 2");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				Usuario professor = carrega(rs);
				professores.add(professor);
			}
			c.close();
			return professores;

		} catch (SQLException e)
		{
			log("UserDAO.listaProfessores: " + e.getMessage());
			return null;
		}	
	}
	/**
	 * Carrega um usuário, dado seu e-mail
	 */
	public Usuario carregaUsuarioEmail(String email)
	{
		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		try
		{
			PreparedStatement ps = c.prepareStatement("SELECT * FROM Usuario WHERE email = ?");
			ps.setString(1, email);

			ResultSet rs = ps.executeQuery();
			Usuario item = rs.next() ? carrega(rs) : null;
			
			c.close();
			return item;

		} catch (SQLException e)
		{
			log("UserDAO.carregaUsuarioEmail: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Conta o numero de usuários que atendem a um filtro
	 */
	public int conta(String filtroNome, String filtroEmail)
	{
		String SQL = "SELECT COUNT(*) " + 
					 "FROM Usuario " + 
					 "WHERE nome LIKE ? " + 
					 "AND email LIKE ? ";
		
		Connection c = getConnection();
		
		if (c == null)
			return 0;
		
		try
		{
			PreparedStatement ps = c.prepareStatement(SQL);
			ps.setString(1, "%" + filtroNome + "%");
			ps.setString(2, "%" + filtroEmail + "%");

			ResultSet rs = ps.executeQuery();
			int count = rs.next() ? rs.getInt(1) : 0;

			c.close();
			return count;

		} catch (SQLException e)
		{
			log("UserDAO.conta: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Retorna a lista de usuários registrados no sistema que atendem a um filtro
	 */
	public List<Usuario> lista(int pagina, int tamanhoPagina, CampoOrdenacaoUsuario campoOrdenacao, CriterioOrdenacaoUsuario criterioOrdenacao, String nomeFiltro, String filtroEmail)
	{
		String SQL = "SELECT * " + 
					 "FROM User " + 
					 "WHERE nome LIKE ? " + 
					 "AND email LIKE ? ";
		
		String SQL_PAGE = "LIMIT ? OFFSET ?";

		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		List<Usuario> lista = new ArrayList<Usuario>();
		
		try
		{
			PreparedStatement ps = c.prepareStatement(SQL + campoOrdenacao.getClause(criterioOrdenacao) + SQL_PAGE);
			ps.setString(1, "%" + nomeFiltro + "%");
			ps.setString(2, "%" + filtroEmail + "%");
			ps.setInt(3, tamanhoPagina);
			ps.setInt(4, pagina * tamanhoPagina);
			ResultSet rs = ps.executeQuery();

			while (rs.next())
			{
				Usuario item = carrega(rs);
				lista.add(item);
			}

			c.close();

		} catch (SQLException e)
		{
			log("UserDAO.lista: " + e.getMessage());
		}
		    
		return lista;
	}

	/**
	 * Adiciona um usuário no sistema
	 */
	public boolean criaNovoUsuario(Usuario usuario)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call UsuarioInsere(?, ?, ?, ?, ?)}");
			cs.setString(1, usuario.getNome());
			cs.setString(2, usuario.getUsername());
			cs.setString(3, usuario.getPassword());
			cs.setInt(4, usuario.getPapel().getCodigo());
			cs.registerOutParameter(5, Types.INTEGER);
			cs.execute();
			usuario.setId(cs.getInt(5));
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("UserDAO.criaNovo: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Atualiza a senha de um usuário
	 */
	public boolean atualizaSenha(int id, String senha)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call UsuarioTrocaSenha(?, ?)}");
			cs.setInt(1, id);
			cs.setString(2, senha);
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("UserDAO.atualizaSenha: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Registra o login de um usuário com sucesso
	 */
	public boolean registraLoginSucesso(String email)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call UsuarioRegistraLoginSucesso(?)}");
			cs.setString(1, email);
			cs.executeUpdate();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("UserDAO.registraLoginSucesso: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Registra o login de um usuário com falha
	 */
	public boolean registraLoginFalha(String email)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call UsuarioRegistraLoginFalha(?)}");
			cs.setString(1, email);
			cs.executeUpdate();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("UserDAO.registraLoginFalha: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Armazena um token de troca de senha para um usuário
	 */
	public boolean salvaTokenLogin(int id, String token)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call UsuarioRegistraTokenResetSenha(?, ?)}");
			cs.setInt(1, id);
			cs.setString(2, token);
			cs.executeUpdate();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("UserDAO.salvaTokenLogin: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Verifica se o token de login de um usuário é válido
	 */
	public boolean verificaValidadeTokenLogin(String email, String token, int maximoHoras) 
	{
		String SQL = "SELECT dataTokenLogin, NOW() " +
					 "FROM Usuario " + 
					 "WHERE email = ? " +
					 "AND tokenLogin = ?";

		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			PreparedStatement ps = c.prepareStatement(SQL);
			ps.setString(1, email);
			ps.setString(2, token);

			ResultSet rs = ps.executeQuery();
			long hours = Integer.MAX_VALUE;

			if (rs.next())
			{
				DateTime dateToken = DateUtils.toDateTime(rs.getTimestamp(1));
				DateTime dateNow = DateUtils.toDateTime(rs.getTimestamp(2));
				hours = (dateNow.getMillis() - dateToken.getMillis()) / (60 * 60 * 1000);
			}

			c.close();
			return (hours < maximoHoras);

		} catch (SQLException e)
		{
			log("UserDAO.verificaValidadeTokenLogin: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Altera o edital selecionado por um usuário
	 */
	public boolean mudaEditalSelecionado(int idUsuario, int idEdital) 
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call UsuarioMudaEditalSelecionado(?, ?)}");
			cs.setInt(1, idUsuario);
			cs.setInt(2, idEdital);
			cs.executeUpdate();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("UserDAO.mudaEditalSelecionado: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Enumeracao dos campos de ordenacao de usuários
	 * 
	 * @author Marcio Barros
	 */
	public enum CampoOrdenacaoUsuario
	{
		None(""),
		ID("id"),
		Name("nome"),
		Email("email");
		
		private String field;
		
		private CampoOrdenacaoUsuario(String field)
		{
			this.field = field;
		}
		
		public String getClause(CriterioOrdenacaoUsuario type)
		{
			if (field.length() == 0)
				return "";
			
			return "ORDER BY " + field + " " + type.getClause() + " ";
		}
	}
	
	/**
	 * Enumeracao dos tipos de ordenacao de usuários
	 * 
	 * @author Marcio Barros
	 */
	public enum CriterioOrdenacaoUsuario
	{
		ASC("ASC"),
		DESC("DESC");
		
		private @Getter String clause;
		
		private CriterioOrdenacaoUsuario(String campo)
		{
			this.clause = campo;
		}
	}
}