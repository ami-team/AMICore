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
    private static final org.slf4j.Logger LOG = LogSingleton.getLogger(LDAPUserValidator.class.getSimpleName());
    /*----------------------------------------------------------------------------------------------------------------*/

    private static final String LDAP_URL = "ldap.url";

    private static final String LDAP_USERNAME = "ldap.username";

    private static final String LDAP_PASSWORD = "ldap.password";

    private static final String LDAP_BASE = "ldap.base";

    private static final String LDAP_FIRSTNAME_FIELD = "ldap.firstname_field";

    private static final String LDAP_LASTNAME_FIELD = "ldap.lastname_field";

    private static final String LDAP_EMAIL_FIELD = "ldap.email_field";

    private static final String LDAP_USERNAME_FIELD = "ldap.username_field";
    // NB: Hypothesis that this is to use in the same fieldname to use in the dn (ex: uid=..., + base ldap)
    //      AND a field available in the LDAP entry itself
    //      AND that these 2 values are the same
    //      (Even though we don't actually use the field in the LDAP entry itself)

    /* NB: Setting them once in a constructor would make the class stateful, and less robust to config change => better without */

    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean check(@NotNull Mode mode, @NotNull Bean bean) throws Exception
    {
        LOG.info("Check, mode=%s, LDAP_URL=%s, LDAP_BASE=%s, LDAP_USERNAME=%s,"
            .formatted(
            mode,
            requiredProperty(LDAP_URL),
            requiredProperty(LDAP_BASE),
            ConfigSingleton.getProperty(LDAP_USERNAME)
            ));
        
        switch(mode)
        {
            case AUTH:
                return this.authenticate_and_set_bean_if_valid(bean);
                
            case ADD:
                // Called from FrontEnd.java's createNewUser(): not sure why we would ever want to refuse at this stage => always true
                return true; 

          // Not Implemented:
              // ATTACH, DETACH, <- Not sure what they're for
                // PASSWORD <- for updating password <- not for LDAP
                // INFO <- for updating info (email, ...) <- not for LDAP
            default:  
                return false; 
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    private UserInfo findUser(@NotNull String username) throws Exception
    {
        try {
            /*--------------------------------------------------------------------------------------------------------*/

            DirContext ctx = new InitialDirContext(
                        this.createEnvironment(
                            ConfigSingleton.getProperty(LDAP_USERNAME),
                            ConfigSingleton.getProperty(LDAP_PASSWORD)
                        )
                    );

            SearchControls controls = new SearchControls();

            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            controls.setCountLimit(2);
            controls.setReturningAttributes(new String[] {
                requiredProperty(LDAP_USERNAME_FIELD),
                requiredProperty(LDAP_FIRSTNAME_FIELD),
                requiredProperty(LDAP_LASTNAME_FIELD),
                requiredProperty(LDAP_EMAIL_FIELD)
            });

            /*--------------------------------------------------------------------------------------------------------*/

            try {
                NamingEnumeration<SearchResult> results = ctx.search(
                                requiredProperty(LDAP_BASE), // <=> -b (base dn for search) parameter of ldapsearch
                                "("+requiredProperty(LDAP_USERNAME_FIELD)+"=" + escapeLdapValue(username) + ")",
                                controls
                            );

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

                userInfo.username = getAttribute(attributes, requiredProperty(LDAP_USERNAME_FIELD));
                userInfo.firstName = getAttribute(attributes, requiredProperty(LDAP_FIRSTNAME_FIELD));
                userInfo.lastName = getAttribute(attributes, requiredProperty(LDAP_LASTNAME_FIELD));
                userInfo.email = getAttribute(attributes, requiredProperty(LDAP_EMAIL_FIELD));

                if(Empty.is(userInfo.username, Empty.STRING_NULL_EMPTY_BLANK))
                {
                    return null;
                }

                return userInfo;
            }
            catch(Exception e)
            {
                LOG.error("Exception while processing findUser results:" + e);
                throw new RuntimeException(e); /* TODO: better exception handling */
            }

            /*--------------------------------------------------------------------------------------------------------*/
        }
        catch(Exception e)
        {
            LOG.error("Exception while launching findUser request:" + e);
            throw new RuntimeException(e); /* TODO: better exception handling */
        }
}

    /*----------------------------------------------------------------------------------------------------------------*/

    private boolean authenticate_and_set_bean_if_valid(@NotNull Bean bean) throws Exception
    {
        String LDAP_username = bean.getSsoUsername(); 
        if(this.authenticate(LDAP_username, bean.getPasswordNew())) {
            UserInfo userInfo = this.findUser(LDAP_username);
            // bean.setSsoUsername(username); We suppose this is already done in the call to authenticate => no need
            bean.setFirstName(userInfo.firstName);
            bean.setLastName(userInfo.lastName);
            bean.setEmail(userInfo.email);

            return true;
        }

        return false;

    }

    private boolean authenticate(@Nullable String username, @Nullable String password)
    {
        LOG.info("authenticate: user = %s".formatted(username));
        try
        {
            DirContext ctx = new InitialDirContext(this.createEnvironment(username, password));
			// The call to the LDAP is done implicility in the constructor
            return true;
        }
        catch(Exception e)
        {
            LOG.error("Exception while authenticating: for " + username + ": " + e);
            return false;
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    private Hashtable<String, String> createEnvironment(@Nullable String username, @Nullable String password)
    {
        LOG.info("createEnvironment for user %s".formatted(username));
        Hashtable<String, String> env = new Hashtable<>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, requiredProperty(LDAP_URL));
        // env.put(Context.SECURITY_AUTHENTICATION, "simple");
		// -> Not necessary because "If this property is not set then its default value is none, unless the java.naming.security.credentials property is set, in which case the default value is simple" (https://docs.oracle.com/javase/8/docs/technotes/guides/jndi/jndi-ldap-gl.html#authentication )
		// -> This will auto-adjust according to if the SECURITY_CREDENTIALS is set or not (below) (according to if password is defined or not)
         
        if(!Empty.is(username, Empty.STRING_NULL_EMPTY_BLANK))
        {
            env.put(Context.SECURITY_PRINCIPAL, requiredProperty(LDAP_USERNAME_FIELD) + "=" + username + "," + requiredProperty(LDAP_BASE));
            // <=> -D (bind DN) parameter of ldapsearch
        }

        if(!Empty.is(password, Empty.STRING_NULL_EMPTY_BLANK)) 
        {
            env.put(Context.SECURITY_CREDENTIALS, password); //
            // <=> -w/W (password) parameter of ldapsearch
        }
        return env;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    @Contract(pure = true)
    private static String requiredProperty(@NotNull String key)
    {
        String value = ConfigSingleton.getProperty(key);

        if(Empty.is(value, Empty.STRING_NULL_EMPTY_BLANK))
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
        // TODO: don't hardcode this, get it from the LDAP_* fields declared at the beginning of the class
        return "Required properties:\n"
            + "  ldap.url\n"
            + "  ldap.base\n"
            + "  ldap.username_field\n"
            + "  ldap.firstname_field\n"
            + "  ldap.lastname_field\n"
            + "  ldap.email_field\n"
            + "\n"
            + "Optional properties:\n"
            + "  ldap.username\n"
            + "  ldap.password\n";
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}
