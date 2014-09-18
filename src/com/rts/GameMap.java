package com.rts;

import com.rts.appframework.Building;
import com.rts.appframework.Game;
import com.rts.appframework.Player;
import com.rts.appframework.Unit;
import com.rts.commandprocessor.ConstructBuildingCommand;
import com.rts.commandprocessor.ConstructUnitCommand;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
 
/**
 * Grid layout builder for the game map and UI.
 * @author Korie Seville
 *
 */
public class GameMap extends Activity {
 
	GridView gridView;
	static GamemapAdapter gma;
	static View view;
	/**
	 * game map activity object
	 */
	public static GameMap gameMapActivity;
	static TextView resourcesText;
	static TextView unitType;
	static TextView hp;
	static TextView power;
	static TextView level;
	static TextView experience;
	static TextView buildingType;
	static TextView buildingHP;
	static Button scout;
	static Button highpower;
	static Button highhp;
	static Button constructionCenter;
	static Button resourceGenerator;
	static Button cancel;
	static Button deselectUnit;
	OnItemClickListener defaultListener;
	boolean unitSelected = false;
	Unit selectedUnit = null;
	Object selectedObject = null;
	Building selectedBuilding;
 
	/**
	 * Actions to take when the activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		gameMapActivity = this;
		setContentView(R.layout.activity_game_map);
		
		gridView = (GridView) findViewById(R.id.gridView);
		resourcesText = (TextView) findViewById(R.id.Resources);
		unitType = (TextView) findViewById(R.id.unitType);
		hp = (TextView) findViewById(R.id.hp);
		power = (TextView) findViewById(R.id.power);
		level = (TextView) findViewById(R.id.level);
		experience = (TextView) findViewById(R.id.experience);
		scout = (Button) findViewById(R.id.createScout);
		highpower = (Button) findViewById(R.id.createHighPowerUnit);
		highhp = (Button) findViewById(R.id.createHighHPUnit);
		constructionCenter = (Button) findViewById(R.id.createConstructionCenter);
		resourceGenerator = (Button) findViewById(R.id.createResourceGenerator);
		cancel = (Button) findViewById(R.id.cancel);
		deselectUnit = (Button) findViewById(R.id.deselectUnit);
		buildingType = (TextView) findViewById(R.id.buildingType);
		buildingHP = (TextView) findViewById(R.id.buildingHP);
		
		Object[][] temp = Game.getGameMap();
		Object[] entities = new Unit[temp.length * temp.length];
		entities = convertTo1DArray(temp);
		gma = new GamemapAdapter(this, entities);
		gridView.setAdapter(gma);
		
		defaultListener = new OnItemClickListener()
		{	
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			{
				int yposition = (int) position / 10;
				int xposition = position - (yposition * 10);
				Game game = Game.getInstance();
				selectedObject = game.getObjectAtPosition(yposition, xposition);
				
				if(selectedObject instanceof Unit && !unitSelected)
				{
					unitType.setVisibility(View.VISIBLE);
					hp.setVisibility(View.VISIBLE);
					power.setVisibility(View.VISIBLE);
					level.setVisibility(View.VISIBLE);
					experience.setVisibility(View.VISIBLE);
					scout.setVisibility(View.GONE);
					highpower.setVisibility(View.GONE);
					highhp.setVisibility(View.GONE);
					constructionCenter.setVisibility(View.GONE);
					resourceGenerator.setVisibility(View.GONE);
					buildingType.setVisibility(View.GONE);
					buildingHP.setVisibility(View.GONE);
					deselectUnit.setVisibility(View.VISIBLE);
					selectedBuilding = null;
					
					if(((Unit) selectedObject).getOwner() == Game.getActivePlayer())
					{
						unitSelected = true;
						selectedUnit = (Unit) selectedObject;
						Log.v(null, "Unit array position: [" + selectedUnit.getY() + "][" + selectedUnit.getX() + "]");	
						
						experience.setText("Experience: " + selectedUnit.getExperience());
						level.setText("Level: " + selectedUnit.getLevel());
						power.setText("Power: " + selectedUnit.getPower());
						hp.setText("HP: " + selectedUnit.getHP());
						if(selectedUnit.getType() == 1)
							unitType.setText("Unit Type: Scout Unit");
						else if(selectedUnit.getType() == 2)
							unitType.setText("Unit Type: High Power");
						else if(selectedUnit.getType() == 3)
							unitType.setText("Unit Type: High HP");
						return;
					}
				}
				
				else if(unitSelected)
				{
					selectedBuilding = null;
					deselectUnit.setVisibility(View.GONE);
					Object selectedObject2 = game.getObjectAtPosition(yposition, xposition);
					if(selectedObject2 instanceof Unit)
					{
						Unit otherUnit = (Unit) selectedObject2;
						if(otherUnit.getOwner() == Game.getAiPlayer())
						{
							selectedUnit.setTarget(otherUnit);
							selectedUnit.move(otherUnit.getX(), otherUnit.getY() - 1);
							selectedUnit = null;
							unitSelected = false;
							gridView.setOnItemClickListener(defaultListener);
						}
					}
					else if(selectedObject2 instanceof Building)
					{
						Building building = (Building) selectedObject2;
						if(building.getOwner() == Game.getAiPlayer())
						{
							selectedUnit.setBuildingTarget(building);
							selectedUnit.move(building.getX(), building.getY() - 1);
							selectedUnit = null;
							unitSelected = false;
							gridView.setOnItemClickListener(defaultListener);
						}
						else
						{
							selectedUnit = null;
							unitSelected = false;
							gridView.setOnItemClickListener(defaultListener);
						}
					}
					
					else
					{
						game = Game.getInstance();
						yposition = (int) position / 10;
						xposition = position - (yposition * 10);
						selectedUnit.move(xposition, yposition);
						selectedUnit = null;
						unitSelected = false;
					}
				}
				
				else if (selectedObject instanceof Building)
				{
					selectedBuilding = (Building) selectedObject;

					if (selectedBuilding.getOwnerID().compareTo(Game.getActivePlayer().getPlayerName()) == 0)
					{

						unitType.setVisibility(View.GONE);
						hp.setVisibility(View.GONE);
						power.setVisibility(View.GONE);
						level.setVisibility(View.GONE);
						experience.setVisibility(View.GONE);
						buildingType.setVisibility(View.VISIBLE);
						buildingHP.setVisibility(View.VISIBLE);
						deselectUnit.setVisibility(View.GONE);

						if (selectedBuilding.getType() == 1)
						{
							scout.setVisibility(View.GONE);
							highpower.setVisibility(View.GONE);
							highhp.setVisibility(View.GONE);
							constructionCenter.setVisibility(View.VISIBLE);
							resourceGenerator.setVisibility(View.VISIBLE);
							buildingType.setText("Main Base");
							buildingHP.setText("HP: " + selectedBuilding.getHP());
						} else if (selectedBuilding.getType() == 2)
						{
							constructionCenter.setVisibility(View.GONE);
							resourceGenerator.setVisibility(View.GONE);
							scout.setVisibility(View.VISIBLE);
							highpower.setVisibility(View.VISIBLE);
							highhp.setVisibility(View.VISIBLE);
							buildingType.setText("Construction Center");
							buildingHP.setText("HP: " + selectedBuilding.getHP());
						} else if (selectedBuilding.getType() == 3)
						{
							constructionCenter.setVisibility(View.GONE);
							resourceGenerator.setVisibility(View.GONE);
							scout.setVisibility(View.GONE);
							highpower.setVisibility(View.GONE);
							highhp.setVisibility(View.GONE);
							buildingType.setText("Resource Generator");
							buildingHP.setText("HP: " + selectedBuilding.getHP());
						}
					}
				}
			}
		};
		
		gridView.setOnItemClickListener(defaultListener);
		
		Game game = Game.getInstance();
		game.startClock();
	}
	
	/**
	 * convert passed in 2D array to 1d array for handling.
	 */
	private static Object[] convertTo1DArray(Object[][] entities)
	{
		Object[] oneDimensionArray = new Object[entities.length * entities.length];
		for(int i = 0; i < entities.length; i++)
		{
			for(int j = 0; j < entities[i].length; j++)
			{
				oneDimensionArray[(i*entities.length) + j] = entities[i][j];
			}
		}
		return oneDimensionArray;
	} 
	
	/**
	 * Creates a scout unit
	 * @param view - default view
	 */
	@SuppressWarnings("unused")
	public void createScout(View view)
	{
		Player activePlayer = Game.getActivePlayer();
		ConstructUnitCommand uc = new ConstructUnitCommand(selectedBuilding, 1, activePlayer);
		displayToastMessageInGame("Scout unit construction in progress.");
	}
	
	/**
	 * Creates a high powered unit
	 * @param view - default view
	 */
	@SuppressWarnings("unused")
	public void createHighPowerUnit(View view)
	{
		Player activePlayer = Game.getActivePlayer();
		ConstructUnitCommand uc = new ConstructUnitCommand(selectedBuilding, 2, activePlayer);
		displayToastMessageInGame("High Power unit construction in progress.");
	}
	
	/**
	 * Creates a high hp unit
	 * @param view - default view
	 */
	@SuppressWarnings("unused")
	public void createHighHPUnit(View view)
	{
		Player activePlayer = Game.getActivePlayer();
		ConstructUnitCommand uc = new ConstructUnitCommand(selectedBuilding, 3, activePlayer);
		displayToastMessageInGame("High HP unit construction in progress.");
	}
	
	/**
	 * Creates a construction center
	 * @param view - default view
	 */
	public void createConstructionCenter(View view)
	{
		constructionCenter.setVisibility(View.GONE);
		resourceGenerator.setVisibility(View.GONE);
		cancel.setVisibility(View.VISIBLE);
		displayToastMessageInGame("Select empty space to place building.");
		
		gridView.setOnItemClickListener(new OnItemClickListener() 
		{
			@SuppressWarnings("unused")
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			{
				int yposition = (int) position / 10;
				int xposition = position - (yposition * 10);
				Game game = Game.getInstance();
				Object selectedObject = game.getObjectAtPosition(yposition, xposition);
				if(selectedObject == null)
				{
					Player activePlayer = Game.getActivePlayer();
					ConstructBuildingCommand bc = new ConstructBuildingCommand(activePlayer.getMainBase(), 2, activePlayer, xposition, yposition);
					cancel.setVisibility(View.GONE);
					gridView.setOnItemClickListener(defaultListener);
					displayToastMessageInGame("Buiding construction in progress.");
				}
				else
				{
					displayToastMessageInGame("Space is not available. Select another space.");
				}
			}
		});
	}
	
	/**
	 * Creates a resource center
	 * @param view - default view
	 */
	public void createResourceGenerator(View view)
	{
		constructionCenter.setVisibility(View.GONE);
		resourceGenerator.setVisibility(View.GONE);
		cancel.setVisibility(View.VISIBLE);
		displayToastMessageInGame("Select empty space to place building.");
		
		gridView.setOnItemClickListener(new OnItemClickListener() 
		{
			@SuppressWarnings("unused")
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
			{
				int yposition = (int) position / 10;
				int xposition = position - (yposition * 10);
				Game game = Game.getInstance();
				Object selectedObject = game.getObjectAtPosition(yposition, xposition);
				if(selectedObject == null)
				{
					Player activePlayer = Game.getActivePlayer();
					ConstructBuildingCommand bc = new ConstructBuildingCommand(activePlayer.getMainBase(), 3, activePlayer, xposition, yposition);
					cancel.setVisibility(View.GONE);
					gridView.setOnItemClickListener(defaultListener);
					displayToastMessageInGame("Buiding construction in progress.");
				}
				else
				{
					displayToastMessageInGame("Space is not available. Select another space.");
				}
			}
		});
	}
	
	/**
	 * Updates the game grid
	 */
	public static void update()
	{
		Object[][] temp = Game.getGameMap();
		Object[] units = new Unit[temp.length * temp.length];
		units = convertTo1DArray(temp);
		gma.entities = units;
		gma.notifyDataSetChanged();
		updateResourceTextView();
		updateUnitTextView();
	}
	
	/**
	 * Updates the resources textview
	 */
	public static void updateResourceTextView()
	{
		resourcesText.setText("Available Resources: " + Game.getActivePlayer().getResources());
		resourcesText.invalidate();
	}
	
	/**
	 * Updates the unit text view
	 */
	public static void updateUnitTextView()
	{
		unitType.invalidate();
		hp.invalidate();
		power.invalidate();
		level.invalidate();
		experience.invalidate();
		buildingHP.invalidate();
	}
	
	/**
	 * Cancels the construction of a building
	 * @param view - default view
	 */
	public void cancelBuildingPlacement(View view)
	{
		gridView.setOnItemClickListener(defaultListener);
		cancel.setVisibility(View.GONE);
		displayToastMessageInGame("Building construction cancelled.");
	}
	
	/**
	 * De-selects the selected unit
	 * @param view - default view
	 */
	public void deselectUnit(View view)
	{
		unitSelected = false;
		selectedUnit = null;
		selectedObject = null;
		gridView.setOnItemClickListener(defaultListener);
		deselectUnit.setVisibility(View.GONE);
	}
	
	/**
	 * Defines what should run during the onStop method (when the activity loses focus)
	 */
	@Override
	public void onStop()
	{
		if(!Game.isGameOver())
		{
			Game game = Game.getInstance();
			Game.saveGamestateToDatabase();
			try
			{
				game.stopClock();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		super.onStop();
	}
	
	/**
	 * Defines what should run during the onRestart method (when the app comes back into focus)
	 */
	@Override
	public void onRestart()
	{
		super.onRestart();
		Game game = Game.getInstance();
		game.reinstantiateCommands();
		game.clearCommandDatabase();
		game.startClock();
	}
	
	/**
	 * Displays a toast message in the game screen
	 * @param message - message to display
	 */
	public void displayToastMessageInGame(String message)
	{
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();	
	}
}