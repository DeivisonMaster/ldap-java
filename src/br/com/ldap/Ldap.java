package br.com.ldap;

import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class Ldap {
	
	public static void parametrosConexao(String url, String usuarioDominio, String usuarioBusca, String senha, String dominio1, String dominio2) {
		Properties initialProperties = new Properties();
		
		initialProperties.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		initialProperties.put(Context.PROVIDER_URL, url);
		initialProperties.put(Context.SECURITY_PRINCIPAL, usuarioDominio);
		initialProperties.put(Context.SECURITY_CREDENTIALS, senha);
		
		conectaNoActiveDirectory(initialProperties, usuarioBusca, dominio1, dominio2);
		
	}
	
	public static void conectaNoActiveDirectory(Properties initialProperties, String usuarioBusca, String dominio1, String dominio2) {
		
		try {
			DirContext context = new InitialDirContext(initialProperties);
			
			configuraFiltrosDeBuscaNoActiveDirectory(context, usuarioBusca, dominio1, dominio2);
			
		} catch (AuthenticationException authEx) {
			System.out.println("Erro na autenticação! ");
			authEx.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void configuraFiltrosDeBuscaNoActiveDirectory(DirContext context, String usuarioBusca, String dominio1, String dominio2) {
		String searchFilter = "(sAMAccountName=" + usuarioBusca + ")";
		String[] atributosRequeridos = { "sn", "cn", "mail", "userPrincipalName", "password", "sAMAccountName", "st",
				"title", "distinguishedname", "memberof", "l", "company", "thumbnailPhoto"};
		
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(atributosRequeridos);
		
		try {
			NamingEnumeration<SearchResult> usuarios = context.search("dc=your_domain_before@,dc=your_domain_after@", searchFilter, controls);
			// exemplo: dc=xxx,dc=yyy
			
			montaObjetoUsuarioNoActiveDirectory(usuarios);
			
		} catch (NamingException e) {
			System.out.println("Erro ao listar dados do usuario!");
			e.printStackTrace();
		}
	}
	

	private static void montaObjetoUsuarioNoActiveDirectory(NamingEnumeration<SearchResult> usuarios) {
		SearchResult searchResult	= null;
		String nome = null;
		String email = null;
		String nomeUsuario = null;
		// String senha = null;
		String login = null;
		String sobreNome = null;
		String cargo = null;
		String estadoNatal = null;
		String dadosGerais = null;
		String membroDe = null;
		String cidade = null;
		String lotacao = null;
		byte[] foto = null;
		
		for (int i = 0; i < 1; i++) {
			try {
				searchResult = (SearchResult) usuarios.next();
				
				Attributes attr=searchResult.getAttributes();
				
				nome = attr.get("cn").get(0).toString();
				sobreNome = attr.get("sn").get(0).toString();
				email = attr.get("mail").get(0).toString();
				nomeUsuario = attr.get("userPrincipalName").get(0).toString();
				// senha = attr.get("password").get(0).toString();
				login = attr.get("sAMAccountName").get(0).toString();
				estadoNatal = attr.get("st").get(0).toString();
				cargo = attr.get("title").get(0).toString();
				dadosGerais = attr.get("distinguishedname").get(0).toString();
				membroDe = attr.get("memberof").get(0).toString();
				cidade = attr.get("l").get(0).toString();
				lotacao = attr.get("company").get(0).toString();
				foto = (byte[]) attr.get("thumbnailPhoto").get(0);

				System.out.println("Nome = " + nome);
				System.out.println("Sobrenome  = " + sobreNome);
				System.out.println("Email  = " + email);
				System.out.println("Nome de usuário  = " + nomeUsuario);
				// System.out.println("Senha = " + senha);
				System.out.println("login  = " + login);
				System.out.println("Cargo = " + cargo);
				System.out.println("Estado Natal = " + estadoNatal);
				System.out.println("distinguishedname = " + dadosGerais);
				System.out.println("memberof = " + membroDe);
				System.out.println("Cidade = " + cidade);
				System.out.println("Lotação = " + lotacao);
				
				try {
					BufferedImage img = ImageIO.read(new ByteArrayInputStream(foto));

					JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(img)));
					
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				/*
				 * OUTROS ATRIBUTOS EXISTENTES NO ACTIVE DIRECTORY
				 * 
				 *  o		Organization
					ou		Organizational unit
					cn		Common name
					sn		Surname
					givenname	First name
					uid		Userid
					dn		Distinguished name
					mail	Email address
					c		País
					l		Cidade
					st		UF
					title	Cargo
					description	Descrição cargo
					telephonenumber		telefone
					givenname			nome
					distinguishedname	Dados gerais
				 * 
				 * */
				
			} catch (NamingException e) {
				System.out.println("Erro ao consultar por Usuario!");
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		
		String url = "ldap://domain_ad:port";
		String usuarioDominio = "user@domain_ad";
		String usuarioBusca = "name_user";
		String senha = "password";
		String dominio1 = "xxx";
		String dominio2 = "yyy";
		
		parametrosConexao(url, usuarioDominio, usuarioBusca, senha, dominio1, dominio2);
	}
}
