package com.example.smartparking

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.smartparking.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { Firebase.firestore }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMapBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFrag = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFrag.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        db.collection("parkingLots")
            .get()
            .addOnSuccessListener { lotSnaps ->
                lotSnaps.forEachIndexed { index, lotDoc ->
                    val lotId    = lotDoc.id
                    val lotName  = lotDoc.getString("name") ?: lotId
                    val centerLat= lotDoc.getDouble("centerLat") ?: return@forEachIndexed
                    val centerLng= lotDoc.getDouble("centerLng") ?: return@forEachIndexed

                    // Centrează camera pe prima parcare încărcată
                    if (index == 0) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(centerLat, centerLng), 16f
                            )
                        )
                    }

                    loadSpotsForLot(lotId, lotName)
                }

                googleMap.setOnMarkerClickListener { marker ->
                    val tag = marker.tag
                    if (tag is Pair<*, *>) {
                        val (lotId, spotId) = tag as Pair<String, String>
                        onMarkerOptions(lotId, spotId, marker)
                        true
                    } else false
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Nu pot încărca parcări: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun loadSpotsForLot(lotId: String, lotName: String) {
        db.collection("parkingLots").document(lotId)
            .collection("spots")
            .get()
            .addOnSuccessListener { spotSnaps ->
                spotSnaps.forEach { spotDoc ->
                    val spotId = spotDoc.id
                    val lat    = spotDoc.getDouble("latitude") ?: return@forEach
                    val lng    = spotDoc.getDouble("longitude") ?: return@forEach
                    val free   = spotDoc.getBoolean("isFree") ?: false
                    val label  = spotDoc.getString("label") ?: spotId

                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(lat, lng))
                            .title("$lotName – Loc $label")
                            .icon(
                                BitmapDescriptorFactory.defaultMarker(
                                    if (free) BitmapDescriptorFactory.HUE_GREEN
                                    else BitmapDescriptorFactory.HUE_RED
                                )
                            )
                    )
                    marker?.tag = Pair(lotId, spotId)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Eroare încărcare locuri: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun onMarkerOptions(lotId: String, spotId: String, marker: Marker) {
        val options = arrayOf("Rezervă loc (5 lei)", "Navighează aici")
        AlertDialog.Builder(requireContext())
            .setTitle(marker.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> reserveSpot(lotId, spotId, marker)
                    1 -> navigateTo(marker.position)
                }
            }
            .show()
    }

    private fun reserveSpot(lotId: String, spotId: String, marker: Marker) {
        val uid     = auth.currentUser!!.uid
        val userRef = db.collection("users").document(uid)
        val spotRef = db.collection("parkingLots")
            .document(lotId)
            .collection("spots")
            .document(spotId)
        val cost    = 5.0

        db.runTransaction { tx ->
            val userSnap      = tx.get(userRef)
            val currentBalance= userSnap.getDouble("balance") ?: 0.0
            if (currentBalance < cost) {
                throw Exception("Fonduri insuficiente (sold: $currentBalance lei)")
            }
            tx.update(userRef, "balance", currentBalance - cost)
            tx.update(spotRef, mapOf("isFree" to false, "reservedBy" to uid))
        }
            .addOnSuccessListener {
                Toast.makeText(context, "Rezervare reușită! –5 lei", Toast.LENGTH_SHORT).show()
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    if (e.message?.contains("Fonduri insuficiente") == true)
                        "Nu ai destule fonduri."
                    else
                        "Eroare rezervare: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun navigateTo(position: LatLng) {
        val uri = Uri.parse("google.navigation:q=${position.latitude},${position.longitude}")
        Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
