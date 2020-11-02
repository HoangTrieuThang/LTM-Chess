package mychessmate.network;



public enum Connection_info
{

    all_is_ok(0),
    err_bad_table_ID(1),
    err_table_is_full(2),
    err_game_without_observer(3),
    err_bad_password(4);
    private int value;

    Connection_info(int value)
    {
        this.value = value;
    }

    public static Connection_info get(int id)
    {
        switch (id)
        {
            case 0:
                return Connection_info.all_is_ok;
            case 1:
                return Connection_info.err_bad_table_ID;
            case 2:
                return Connection_info.err_table_is_full;
            case 3:
                return Connection_info.err_game_without_observer;
            case 4:
                return Connection_info.err_bad_password;
            default:
                return null;
        }
    }

    public int getValue()
    {
        return value;
    }
}