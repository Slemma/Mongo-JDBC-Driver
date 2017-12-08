import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Igor Shestakov.
 */
public class Utils
{
	public static String AdjustString(String str ,int symbolsCount)
	{
		if (str == null)
			str = "null";

		if (str.length()>symbolsCount){
			return str.substring(0,symbolsCount);
		}
		else {
			char[] chars = new char[symbolsCount-str.length()];
			Arrays.fill(chars, ' ');
			String emptyText = new String(chars);

			return str + emptyText;
		}
	}

	public static int printResultSet(ResultSet rs){
		return printResultSet(rs, true);
	}

	public static int printResultSet(ResultSet rs, Boolean printRowNumber){

		final int columnWidth = 26;
		Integer counter=0;

		ArrayList<String> columnNames = new ArrayList<>();
		try
		{
			if (printRowNumber)
				System.out.print(AdjustString("Row №", columnWidth) + " | ");

			//HEADER
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
			{
				columnNames.add(rs.getMetaData().getColumnName(i));
				System.out.print(AdjustString(columnNames.get(i - 1), columnWidth) + " | ");
			}

			System.out.print("\n");
			int columnsCount = columnNames.size();
			if (printRowNumber)
				columnsCount++;
			char[] chars = new char[(columnWidth+3)*columnsCount];
			Arrays.fill(chars, '-');
			System.out.println(new String(chars));

			//BODY
			while (rs.next())
			{
				String rowStr = "";
				counter++;

				if (printRowNumber)
					rowStr += AdjustString(counter.toString(), columnWidth) + " | ";

				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++)
				{
					rowStr += AdjustString(rs.getString(columnNames.get(i)), columnWidth) + " | ";
				}
				System.out.println(rowStr);
			}

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return counter;
	}

}
