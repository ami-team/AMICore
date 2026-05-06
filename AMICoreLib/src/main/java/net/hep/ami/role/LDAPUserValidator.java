package net.hep.ami.role;

/*--------------------------------------------------------------------------------------------------------------------*/

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

import net.hep.ami.*;
import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

/*--------------------------------------------------------------------------------------------------------------------*/

public class LDAPUserValidator extends UserValidator
{
    /*----------------------------------------------------------------------------------------------------------------*/

    private static final String LDAP_URL = "ldap.url";

    private static final String LDAP_USERNAME = "ldap.username";

    private static final String LDAP_PASSWORD = "ldap.password";

    private static final String LDAP_BASE = "ldap.base";

    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean check(@NotNull Mode mode, @NotNull Bean bean) throws Exception
    {
        switch(mode)
        {
            case ADD:
                return this.checkAdd(bean);
/*
Modes:
		ADD,
		ATTACH,
		DETACH,
		PASSWORD,
		INFO,

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Bean
	{
		@NotNull private String amiUsername;
		@Nullable private String ssoUsername;  <- login LDAP
		@Nullable private String passwordOld;
		@Nullable private String passwordNew;
		@Nullable private String clientDN;
		@Nullable private String issuerDN;
		@Nullable private String firstName;
		@Nullable private String lastName;
		@Nullable private String email;
		@Nullable private String json;
	}

*/
            default:
                return false;
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    private boolean checkAdd(@NotNull Bean bean) throws Exception
    {
        if(Empty.Is(bean.getSsoUsername(), Empty.STRING_NULL_EMPTY_BLANK)
           ||
           Empty.Is(bean.getPasswordNew(), Empty.STRING_NULL_EMPTY_BLANK))
        {
            return false;
        }

        String ssoUsername = bean.getSsoUsername();
        String passwordNew = bean.getPasswordNew();

        UserInfo userInfo = this.findUser(ssoUsername);

        if(userInfo == null)
        {
            return false;
        }

        if(!this.authenticate(userInfo.username, passwordNew))
        {
            return false;
        }

        bean.setAmiUsername(userInfo.username);
        bean.setSsoUsername(userInfo.username);
        bean.setPasswordOld(passwordNew);
        bean.setPasswordNew(passwordNew);

        if(!Empty.Is(userInfo.firstName, Empty.STRING_NULL_EMPTY_BLANK))
        {
            bean.setFirstName(userInfo.firstName);
        }

        if(!Empty.Is(userInfo.lastName, Empty.STRING_NULL_EMPTY_BLANK))
        {
            bean.setLastName(userInfo.lastName);
        }

        if(!Empty.Is(userInfo.email, Empty.STRING_NULL_EMPTY_BLANK))
        {
            bean.setEmail(userInfo.email);
        }

        return true;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    private UserInfo findUser(@NotNull String username) throws Exception
    {
        try(DirContext ctx = new InitialDirContext(
            this.createEnvironment(
                ConfigSingleton.getProperty(LDAP_USERNAME),
                ConfigSingleton.getProperty(LDAP_PASSWORD)
            )
        )) {
            /*--------------------------------------------------------------------------------------------------------*/

            SearchControls controls = new SearchControls();

            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            controls.setCountLimit(2);
            controls.setReturningAttributes(new String[] {
                "uid",
                "firstName",
                "lastName",
                "mail"
            });

            /*--------------------------------------------------------------------------------------------------------*/

            try(NamingEnumeration<SearchResult> results = ctx.search(
                requiredProperty(LDAP_BASE),
                "(uid=" + escapeLdapValue(username) + ")",
                controls
            )) {
                if(!results.hasMore())
                {
                    return null;
                }

                SearchResult result = results.next();

                if(results.hasMore())
                {
                    return null;
                }

                Attributes attributes = result.getAttributes();

                UserInfo userInfo = new UserInfo();

                userInfo.username = getAttribute(attributes, "uid");
                userInfo.firstName = getAttribute(attributes, "firstName");
                userInfo.lastName = getAttribute(attributes, "lastName");
                userInfo.email = getAttribute(attributes, "mail");

                if(Empty.Is(userInfo.username, Empty.STRING_NULL_EMPTY_BLANK))
                {
                    return null;
                }

                return userInfo;
            }

            /*--------------------------------------------------------------------------------------------------------*/
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    private boolean authenticate(@Nullable String username, @Nullable String password)
    {
        try(DirContext ctx = new InitialDirContext(this.createEnvironment(username, password)))
        {
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    private Hashtable<String, String> createEnvironment(@Nullable String username, @Nullable String password)
    {
        Hashtable<String, String> env = new Hashtable<>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, requiredProperty(LDAP_URL));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        if(!Empty.Is(username, Empty.STRING_NULL_EMPTY_BLANK))
        {
            env.put(Context.SECURITY_PRINCIPAL, username);
        }

        if(!Empty.Is(password, Empty.STRING_NULL_EMPTY_BLANK))
        {
            env.put(Context.SECURITY_CREDENTIALS, password);
        }

        return env;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    @Contract(pure = true)
    private static String requiredProperty(@NotNull String key)
    {
        String value = ConfigSingleton.getProperty(key);

        if(Empty.Is(value, Empty.STRING_NULL_EMPTY_BLANK))
        {
            throw new IllegalStateException("Missing configuration property: " + key);
        }

        return value.trim();
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    private static String getAttribute(@Nullable Attributes attributes, @NotNull String name) throws Exception
    {
        if(attributes == null)
        {
            return null;
        }

        Attribute attribute = attributes.get(name);

        if(attribute == null)
        {
            return null;
        }

        Object value = attribute.get();

        return value == null ? null : value.toString();
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    @Contract(pure = true)
    private static String escapeLdapValue(@NotNull String value)
    {
        StringBuilder result = new StringBuilder(value.length());

        for(int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);

            switch(c)
            {
                case '\\':
                    result.append("\\5c");
                    break;

                case '*':
                    result.append("\\2a");
                    break;

                case '(':
                    result.append("\\28");
                    break;

                case ')':
                    result.append("\\29");
                    break;

                case '\0':
                    result.append("\\00");
                    break;

                default:
                    result.append(c);
                    break;
            }
        }

        return result.toString();
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    private static class UserInfo
    {
        private String username;

        private String firstName;

        private String lastName;

        private String email;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    @Contract(pure = true)
    public static String help()
    {
        return "Required properties:\n"
            + "  ldap.url\n"
            + "  ldap.base\n"
            + "\n"
            + "Optional properties:\n"
            + "  ldap.username\n"
            + "  ldap.password\n";
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}