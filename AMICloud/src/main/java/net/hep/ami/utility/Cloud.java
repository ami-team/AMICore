package net.hep.ami.utility;

import java.io.*;
import java.util.*;

import org.jclouds.*;
import org.jclouds.openstack.nova.v2_0.*;
import org.jclouds.openstack.nova.v2_0.domain.*;
import org.jclouds.openstack.nova.v2_0.options.*;
import org.jclouds.openstack.nova.v2_0.features.*;

import com.google.common.io.*;
import com.google.common.collect.*;

public class Cloud implements Closeable {
	/*---------------------------------------------------------------------*/

	public static class CloudServer {

		public String region;
		public String serverID;
		public String serverLabel;

		public CloudServer(String _region, String _serverID, String _serverLabel) {

			region = _region;
			serverID = _serverID;
		}

		public String toString() {

			return String.format("region: %s, serverID: %s, serverLabel: %s",
				region, serverID, serverLabel
			);
		}
	}

	/*---------------------------------------------------------------------*/

	public static class CloudFlavor {

		public String region;
		public String flavorID;
		public String flavorLabel;

		public CloudFlavor(String _region, String _flavorID, String _flavorLabel) {
			region = _region;
			flavorID = _flavorID;
			flavorLabel = _flavorLabel;
		}

 		public String toString() {

			return String.format("region: %s, flavorID: %s, flavorLabel: %s",
				region, flavorID, flavorLabel
			);
		}
	}

	/*---------------------------------------------------------------------*/

	public static class CloudImage {

		public String region;
		public String imageID;
		public String imageLabel;

		public CloudImage(String _region, String _imageID, String _imageLabel){
			region = _region;
			imageID = _imageID;
			imageLabel = _imageLabel;
		}

		public String toString() {

			return String.format("region: %s, imageID: %s, imageLabel: %s",
				region, imageID, imageLabel
			);
		}
	}

	/*---------------------------------------------------------------------*/

	private NovaApi m_novaApi;

	private Set<String> m_regions;

	/*---------------------------------------------------------------------*/

	public Cloud(String endpoint, String identity, String credential) throws Exception {

		m_novaApi = ContextBuilder.newBuilder("openstack-nova")
		                          .endpoint(endpoint)
		                          .credentials(identity, credential)
		                          .buildApi(NovaApi.class)
		;

		m_regions = m_novaApi.getConfiguredRegions();
	}

	/*---------------------------------------------------------------------*/

	public void close() throws IOException {

		Closeables.close(m_novaApi, true);
	}

	/*---------------------------------------------------------------------*/

	public Set<String> listRegions() {

		return m_regions;
	}

	/*---------------------------------------------------------------------*/

	public Set<CloudServer> listServers()
	{
		Set<CloudServer> result = new HashSet<CloudServer>();

		/*-----------------------------------------------------------------*/

		ServerApi serverApi;

		for(String region : m_regions) {

			serverApi = m_novaApi.getServerApi(region);

			for(Server server : serverApi.listInDetail().concat()) {

				result.add(new CloudServer(
					region,
					server.getId(),
					server.getKeyName()
				));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Set<CloudFlavor> listFlavors() {

		Set <CloudFlavor> result = new HashSet<CloudFlavor>();

		/*-----------------------------------------------------------------*/

		FlavorApi flavorApi;

		for(String region : m_regions) {

			flavorApi = m_novaApi.getFlavorApi(region);

			for(Flavor flavor : flavorApi.listInDetail().concat()) {

				result.add(new CloudFlavor(
					region,
					flavor.getId(),
					flavor.getName()
				));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Set<CloudImage> listImages()
	{
		Set <CloudImage> result = new HashSet<CloudImage>();

		/*-----------------------------------------------------------------*/

		ImageApi imageApi;

		for(String region : m_regions) {

			imageApi = m_novaApi.getImageApi(region);

			for(Image image : imageApi.listInDetail().concat())
			{
				result.add(new CloudImage(
					region,
					image.getId(),
					image.getName()
				));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public void serverStart(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.start(serverID);
	}

	/*---------------------------------------------------------------------*/

	public void serverStop(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.stop(serverID);
	}

	/*---------------------------------------------------------------------*/

	public void serverHardReboot(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.reboot(serverID, RebootType.HARD);
	}

	/*---------------------------------------------------------------------*/

	public void serverSoftReboot(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.reboot(serverID, RebootType.SOFT);
	}

	/*---------------------------------------------------------------------*/

	public String bootServer(
		String region,
		String name,
		String imageID,
		String flavorID,
		String keypair,
		String networUUID,
		String fixedIP
	 ) {
		ServerApi serverApi = m_novaApi.getServerApi(region);

		CreateServerOptions options = CreateServerOptions.Builder.keyPairName(
			keypair
		).novaNetworks(
			ImmutableSet.of(
				Network.builder().networkUuid(networUUID).fixedIp(fixedIP).build()
			)
		);

		return serverApi.create(name, imageID, flavorID, options).getId();
	}

	/*---------------------------------------------------------------------*/

	public void rebuildServer(String region, String serverID, String imageID)
	{
		ServerApi serverApi = m_novaApi.getServerApi(region);

		RebuildServerOptions options = RebuildServerOptions.Builder.withImage(
			imageID
		);

		serverApi.rebuild(serverID, options);
	}

	/*---------------------------------------------------------------------*/

	public String createImageFromServer(String region, String imageName, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		return serverApi.createImageFromServer(imageName, serverID);
	}

	/*---------------------------------------------------------------------*/

	public void deleteServer(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.delete(serverID);
	}

	/*---------------------------------------------------------------------*/

	public void deleteFlavor(String region, String flavorID) {

		FlavorApi flavorApi = m_novaApi.getFlavorApi(region);

		flavorApi.delete(flavorID);
	}

	/*---------------------------------------------------------------------*/

	public void deleteImage(String region, String imageID) {

		ImageApi imageApi = m_novaApi.getImageApi(region);

		imageApi.delete(imageID);
	}

	/*---------------------------------------------------------------------*/
}
