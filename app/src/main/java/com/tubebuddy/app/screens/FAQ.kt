package com.tubebuddy.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FAQ(val q: String, val a: String)

fun defaultFaqs(): List<FAQ> = listOf(
    FAQ(
        "How do I keep the tube from clogging when giving meds?",
        "Give one medication at a time. Crush tablets to a fine powder and fully dissolve in warm water. " +
                "Flush with 10–20 mL water before each med, between meds, and after the last med. " +
                "Avoid mixing meds together or putting them into formula."
    ),
    FAQ(
        "The extension set won’t connect or leaks—what can I do?",
        "Check for dried formula on the connector; soak in warm soapy water and rinse. Inspect O-rings; " +
                "replace the extension if the seal looks worn. Keep a couple of spare extensions in your go-bag."
    ),
    FAQ(
        "There’s granulation tissue around the stoma. How do I manage it?",
        "Keep the site clean and dry; minimize moisture under dressings. A thin barrier cream can protect skin. " +
                "Many teams use silver nitrate sticks (if prescribed/trained). Consistent gentle care often reduces regrowth."
    ),
    FAQ(
        "My loved one coughs or vomits during a feed—what now?",
        "Stop the feed, keep them upright, and allow a break. Vent the tube to release gas. " +
                "Resume slower and/or with smaller volumes. If it keeps happening, try room-temp formula and confirm positioning."
    ),
    FAQ(
        "Fastest safe way to unclog a tube without a kit?",
        "Use warm water with a push–pull plunger motion (never force). Let the warm water sit 5–10 minutes and try again. " +
                "Some caregivers use enzyme + bicarbonate mixtures when approved—keep your provider’s recipe handy."
    ),
    FAQ(
        "Pump says “occlusion” over and over. What should I check?",
        "Look for kinks or a closed clamp. Check that the giving set is seated correctly and the roller clamp is fully open. " +
                "Gently warm cold formula and clear any thickened formula from the line."
    ),
    FAQ(
        "How do I keep the stoma from smelling?",
        "Daily soap-and-water clean, thorough dry, and frequent dressing changes. Address small leaks quickly; " +
                "a split gauze or thin foam dressing can catch moisture. If odor persists, re-check balloon volume/fit."
    ),
    FAQ(
        "There’s leakage around the site—any tips?",
        "Mark bumper position and verify balloon volume. Leaks often mean the tube is sitting too loose or the balloon needs refill. " +
                "Protect skin with a barrier film/cream and change damp dressings promptly."
    ),
    FAQ(
        "Preventing bloating with bolus feeds?",
        "Vent before and after, slow the push rate, or split into smaller, more frequent feeds. " +
                "Ensure formula is room temperature and consider a brief pause mid-feed to burp/vent."
    ),
    FAQ(
        "Formula is too cold—how do I warm it safely?",
        "Bring to room temp by setting out or placing the sealed container in warm water. Do not microwave."
    ),
    FAQ(
        "Best way to travel with tube supplies?",
        "Pack a go-kit: spare extensions, syringes, tape, gauze, clamps, water, and at least one backup feed. " +
                "For flights, carry supplies in hand luggage; TSA allows medical liquids—labels and a doctor’s note help."
    ),
    FAQ(
        "How do I keep the tube from pulling at night?",
        "Anchor the line with medical tape or a soft wrap, route tubing under a shirt/onesie, or use an abdominal binder. " +
                "Some pajamas/sleep shirts have interior pockets to keep the line secure."
    ),
    FAQ(
        "My loved one feels self-conscious about their tube. Any ideas?",
        "Covers, belts, and wraps can make the tube less visible and more comfortable. Normalize routine care around family, " +
                "and let them choose clothing that feels protective and confident."
    ),
    FAQ(
        "What should I always keep on hand?",
        "Syringes (a few sizes), extension sets, clean water, gauze/tape, spare tube/port supplies, and an emergency clamp. " +
                "Keep a small travel kit ready in the car or backpack."
    )
)

@Composable
fun FAQListContent(modifier: Modifier = Modifier) {
    val faqs = remember { defaultFaqs() }
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(faqs) { item ->
            FAQRow(item)
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun FAQRow(item: FAQ) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Q: ${item.q}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (expanded) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "A: ${item.a}",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Feeding Tube FAQ") }) }
    ) { padding ->
        FAQListContent(modifier = Modifier.padding(padding))
    }
}
