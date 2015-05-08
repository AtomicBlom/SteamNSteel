package mod.steamnsteel.client.collada;

import mod.steamnsteel.client.collada.model.ColladaModel;
import mod.steamnsteel.utility.log.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Steven on 13/04/2015.
 */
public class ColladaTest {
    public static void main(String[] args) throws JAXBException, IOException, ColladaException {
        ColladaModel colladaModel;
        //Logger.info("Loading accessorTest...");
        //colladaModel = ColladaModelReader.read(new File("C:\\Git\\Minecraft\\SteamNSteel\\tests\accessorTest.dae"));
        //Logger.info("Loading Cube...");
        //colladaModel = ColladaModelReader.read(new File("C:\\Git\\Minecraft\\SteamNSteel\\tests\Cube.dae"));
        Logger.info("Loading PipeWrench...");
        colladaModel = ColladaModelReader.read(new File("C:\\Git\\Minecraft\\SteamNSteel\\tests\\pipeWrench.dae"));

        System.out.print("Loaded.");
    }

}
