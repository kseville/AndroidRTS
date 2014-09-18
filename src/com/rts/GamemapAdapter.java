package com.rts;

import com.rts.appframework.Building;
import com.rts.appframework.Unit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Custom Adapter for customer GridView
 * 
 * @author Korie Seville
 * 
 *         NOTE: Portions of this code were inspired / derived from the
 *         CustomAdapter example found at
 *         http://examples.javacodegeeks.com/android
 *         /core/ui/gridview/android-gridview-example/
 */

public class GamemapAdapter extends BaseAdapter
{
	private Context context;
	protected Object[] entities;

	/**
	 * Custom GameMap Adapter constructor
	 * @param context - application context
	 * @param unitsIn - the game map
	 */
	public GamemapAdapter(Context context, Object[] unitsIn)
	{
		this.context = context;
		entities = unitsIn;
	}

	

	/**
	 * Get the View and inflate it with the layout specified
	 * @param position - the position in the array
	 * @param convertView - the view being used
	 * @param parent - the parent view group
	 * @return the inflated view
	 */
	public View getView(int position, View convertView, ViewGroup parent)
	{

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null)
		{
			gridView = new View(context);
			gridView = inflater.inflate(R.layout.gamemap, null);
		} 
		
		else
		{
			gridView = (View) convertView;
		}
		
		ImageView imView = (ImageView) gridView.findViewById(R.id.flag);

		if(entities[position] instanceof Unit)
		{
			Unit unit = (Unit) entities[position];
			
			if(unit.getOwnerID().compareTo("AI") == 0)
			{
				int unitType = 0;
				try
				{
					unitType = unit.getType();
				} catch (Exception e)
				{
					imView.setImageResource(R.drawable.emptyspace);
				}

				if (unitType == 1)
				{
					imView.setImageResource(R.drawable.squareai);
				} else if (unitType == 3)
				{
					imView.setImageResource(R.drawable.circleai);
				} else if (unitType == 2)
				{
					imView.setImageResource(R.drawable.triangleai);
				}
			}
			
			else
			{
				int unitType = 0;
				try
				{
					unitType = unit.getType();
				} catch (Exception e)
				{
					imView.setImageResource(R.drawable.emptyspace);
				}

				if (unitType == 1)
				{
					imView.setImageResource(R.drawable.squarehuman);
				} else if (unitType == 3)
				{
					imView.setImageResource(R.drawable.circlehuman);
				} else if (unitType == 2)
				{
					imView.setImageResource(R.drawable.trianglehuman);
				}
			}
			
		}
		
		else if(entities[position] instanceof Building)
		{
			Building building = (Building) entities[position];
			
			if(building.getOwnerID().compareTo("AI") == 0)
			{
				int buildingType = 0;
				try
				{
					buildingType = building.getType();
				} catch (Exception e)
				{
					imView.setImageResource(R.drawable.emptyspace);
				}

				if (buildingType == 1)
				{
					imView.setImageResource(R.drawable.aimainbase);
				} else if (buildingType == 2)
				{
					imView.setImageResource(R.drawable.aiconstructioncenter);
				} else if (buildingType == 3)
				{
					imView.setImageResource(R.drawable.airesourcegenerator);
				}
			}
			
			else
			{
				int buildingType = 0;
				try
				{
					buildingType = building.getType();
				} catch (Exception e)
				{
					imView.setImageResource(R.drawable.emptyspace);
				}

				if (buildingType == 1)
				{
					imView.setImageResource(R.drawable.mainbasehuman);
				} else if (buildingType == 2)
				{
					imView.setImageResource(R.drawable.constructioncenterhuman);
				} else if (buildingType == 3)
				{
					imView.setImageResource(R.drawable.resourcegeneratorhuman);
				}
			}
		}
		
		else
		{
			imView.setImageResource(R.drawable.emptyspace);
		}
		
		return gridView;
	}

	/**
	 * @return the length of the array
	 */
	@Override
	public int getCount()
	{
		return entities.length;
	}

	/**
	 * @return the object at the requested position
	 */
	@Override
	public Object getItem(int position)
	{
		return null;
	}

	/**
	 * Return the item id of the object at the specified position
	 * @param position - the position to get
	 * @return the item id of the object at the specified position
	 */
	@Override
	public long getItemId(int position)
	{
		return 0;
	}

}