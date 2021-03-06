package sh.calaba.instrumentationbackend.actions.list;

import java.util.ArrayList;

import sh.calaba.instrumentationbackend.InstrumentationBackend;
import sh.calaba.instrumentationbackend.Result;
import sh.calaba.instrumentationbackend.actions.Action;
import android.widget.ListView;

/**
 * Provides access to a list through its adapter.
 * 
 * args: 
 * <ul> 
 *   <li>1-based list index (first list is used if not specified)</li>
 * </ul>
 * 
 * eg: (all items of the 1st list) <code>performAction( 'get_list_data' )</code>
 * eg: (all items of the 2nd list) <code>performAction( 'get_list_data', 2 )</code>
 * 
 * @return <code>bonusInformation</code>: a JSON-formatted Array<String> that is an
 * Array of objects with a single property "value". The value of this property is generated by calling
 * <code>list.getAdapter().getItem().toString()</code>, so make sure you implement the toString() method 
 * on the objects that go to the adapter AND that it returns a value valid for JSON.
 * 
 * In ruby we can then parse the response:
 * <pre>
 * 		result = performAction('get_list_data')
 * 		bonusInfo = result['bonusInformation'][0]
 * 		JSON.parse(bonusInfo).each do |item|
 * 			puts "#{item["value"]}"
 * 		end
 * </pre>
 * 
 * @author Juan Delgado (juan@ustwo.co.uk)
 */

public class GetListData implements Action {

	@Override
	public Result execute(String... args) {
		
		int listIndex;

		if( args.length == 0 ) {
			listIndex = 0;
		} else {
			listIndex = (Integer.parseInt(args[0]) - 1);
		}
		
		ArrayList<ListView> listViews = InstrumentationBackend.solo.getCurrentListViews();
		
		if( listViews == null || listViews.size() <= listIndex ) {
			return new Result(false, "Could not find list #" + (listIndex + 1));
		}
		
		ListView list = listViews.get(listIndex);
		Result result = new Result(true);
		
		StringBuilder json = new StringBuilder("[");
		
		int count = list.getAdapter().getCount();
		for( int i = 0; i < count; i++ ) {
			json.append("{\"value\": \"");
			json.append(list.getAdapter().getItem(i).toString());
			json.append("\"},");
		}
		
		if(count > 0)
		{
			json.deleteCharAt( json.length() - 1 ); // remove the last comma
		}
		
		json.append("]");
		
		result.addBonusInformation(json.toString());
		
		return result;
	}

	@Override
	public String key() {
		return "get_list_data";
	}
}
