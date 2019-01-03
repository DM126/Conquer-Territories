//import java.util.ArrayList;
//
////UNUSED FOR NOW
//public class World
//{
//	private Province[] provinces;
//	
//	public World()
//	{
//		provinces = new Province[12];
//	}
//	
//	/**
//	 * 
//	 * 
//	 * @param provinceID the id of the province
//	 * @return the province with that id
//	 */
//	public Province getProvince(int provinceID)
//	{
//		return provinces[provinceID - 1];
//	}
//	
//	/**
//	 * Searches the list of provinces for one with a certain name.
//	 * 
//	 * @param provinceName the name of the desired province
//	 * @return the province with that name
//	 */
//	public Province getProvince(String provinceName)
//	{
//		Province p = null;
//		
//		int i = 0;
//		do
//		{
//			if (provinces[i].getName().equals(provinceName))
//				p = provinces[i];
//			else
//				i++;
//		}
//		while (p == null && i < provinces.length);
//		
//		return p;
//	}
//}
