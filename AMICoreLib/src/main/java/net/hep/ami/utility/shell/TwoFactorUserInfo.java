package net.hep.ami.utility.shell;

import org.apache.sshd.client.session.*;
import org.apache.sshd.client.auth.keyboard.*;

import net.hep.ami.utility.*;

import org.jetbrains.annotations.*;

public class TwoFactorUserInfo implements UserInteraction
{
    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable private String m_password;
    @Nullable private String m_tfaCode;

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable private final String m_tfaPrompt;

    private final boolean m_tfaOk;

    /*----------------------------------------------------------------------------------------------------------------*/

    public TwoFactorUserInfo(@Nullable String password, @Nullable String tfaPrompt)
    {
        /*------------------------------------------------------------------------------------------------------------*/

        if(!Empty.is(tfaPrompt, Empty.STRING_NULL_EMPTY_BLANK))
        {
            tfaPrompt = tfaPrompt.toLowerCase();

            this.m_tfaOk = true;
        }
        else
        {
            this.m_tfaOk = false;
        }

        /*------------------------------------------------------------------------------------------------------------*/

        this.m_password = password;
        this.m_tfaPrompt = tfaPrompt;

        /*------------------------------------------------------------------------------------------------------------*/
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public boolean isInteractionAllowed(ClientSession session)
    {
        return true;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public String[] interactive(ClientSession session, String name, String instruction, String lang, String[] prompt, boolean[] echo)
    {
        String[] responses = new String[prompt.length];

        for(int i = 0; i < prompt.length; i++)
        {
            String p = prompt[i].toLowerCase();

            AbstractShell.LOG.info("KBI prompt[{}] = '{}'", i, p);

            /**/ if(p.contains("password"))
            {
                responses[i] = m_password != null ? m_password : "";
            }
            else if(this.m_tfaOk && p.contains(this.m_tfaPrompt))
            {
                responses[i] = m_tfaCode != null ? m_tfaCode : "";
            }
            else
            {
                responses[i] = "";
            }
        }

        return responses;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Override
    public String getUpdatedPassword(ClientSession session, String prompt, String lang)
    {
        return null;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    public void setPassword(String password)
    {
        m_password = password;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    public void set2FACode(String tfaCode)
    {
        m_tfaCode = tfaCode;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}
