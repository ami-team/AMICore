package net.hep.ami.command.hashable;

import java.util.*;

import net.hep.ami.command.*;
import net.hep.ami.utility.*;

public class Utilities
{
    /*----------------------------------------------------------------------------------------------------------------*/

    public static String getNewHash()
    {
        long uuid;

        String hash;

        for(;;)
        {
            uuid = UUID.randomUUID().getMostSignificantBits();

            hash = Base64.getEncoder().encodeToString(Long.toString(uuid < 0 ? -uuid : +uuid).getBytes());

            if(hash.length() >= 8)
            {
                hash = hash.substring(0, 8);

                break;
            }
        }

        return hash;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    public static String getRank(AbstractCommand command, String entity) throws Exception
    {
        String rank = command.getQuerier("self").executeSQLQuery(entity, "SELECT max(`rank`) + 1 FROM `" + entity + "` WHERE `owner` = ?0", command.m_AMIUser).getAll().get(0).getValue(0);

        if(Empty.is(rank, Empty.STRING_AMI_NULL))
        {
            rank = "0";
        }

        return rank;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}
